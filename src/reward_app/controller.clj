(ns reward-app.controller
    (:use clojure.pprint))

(defn- parse
    "Converts a list of pairs to rows of columns"
    [string]
    (map   #(clojure.string/split % #" ") ;; yields a vector [inviter invited] representing an invitation
            (clojure.string/split string #"\n"))) ;; yields a vector of invitations

(defn- updateScoresFunction
    "Recursively updates scores"
    [record id k]
    (if (= id "1")  ;; trivial case: if id is "1" then the propagation of rewards is over as "1" is not
        record      ;; invited by anyone, i.e., it has no parents
        (let [entry (some #(and (contains? (:children %) id) %) record) ;; get the costumer parent 'id'
              index (.indexOf record entry) ;; get index of the parent on 'record'
              increment (reduce * 1 (take k (repeat 1/2)))] ;; get the increment to be made at level 'k'
            (recur (assoc record index                     ;; get a new record with the entry of the
                        (assoc entry :score                ;; parent with the score updated and make a
                            (+ (:score entry) increment))) ;; recursive call to update the parents
                    (:id entry) (inc k)))))                ;; parent

(defn- process
    "Processes invitations made"
    [[[A B] & tail] record updateScores]
    (defn proceed
        "Proceeds with the processing of the invitations if there is any left"
        [tail record updateScores]
        (if tail
            (process tail record updateScores)
            record))
    (defn checkB
        "Checks if the invited person is already a costumer and if not make it so"
        [record inviter B tail]
        (if (not (some #(= (:id %) B) record)) ;; if B is not a costumer already
            (let [record (conj record {:id B :score -1 :children #{}}) ;; conjoin an entry to it in record
                  record (assoc record (.indexOf record inviter)                    ;; associate it to its
                            (assoc inviter :children (conj (:children inviter) B)))];; parent
                 (proceed tail record updateScores)) ;; proceed...
            (proceed tail record updateScores))) ;; if it's a costumer then just proceed
    (if-let [entry (some #(and (= (:id %) A) %) record)] ;; if the inviter is a costumer
        (if (= (:score entry) -1) ;; if he/she wasn't a confirmed costumer
            (let [updatedEntry (assoc entry :score 0) ;; make it so
                  record (updateScores (assoc record (.indexOf record entry) updatedEntry) A 0)]
                (checkB record updatedEntry B tail)) ;; go check the invited person info
            (checkB record entry B tail)) ;; if not just check the invited person info
        (proceed tail record updateScores))) ;; if not, then just proceed to the next invitation

(defn loadFile
    "Loads invitations from file"
    [file]
    (let [record [{:id "1" :score 0 :children #{}}]] ;; record starts with the confirmed costumer "1"
        (trampoline process (parse (slurp file)) record updateScoresFunction))) ;; process invitations

(defn rank
    "Ranks invited people based on their score"
    [record]
    (let [list (sort-by :score (reduce #(conj % (select-keys %2 [:id :score])) [] record)) ;; sort by score
          unproper (take-while #(= (:score %) -1) list) ;; not confirmed costumers are yet unproper
          proper (drop-while #(= (:score %) -1) list)] ;; confirmed costumers are ok
        (concat (reverse proper) (reduce #(conj % (assoc %2 :score 0)) [] unproper)))) ;; return a proper list

(defn invite
    "Processes an invite"
    [record inviter invited]
    (trampoline process [[inviter invited]] record updateScoresFunction))
