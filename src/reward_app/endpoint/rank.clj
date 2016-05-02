(ns reward-app.endpoint.rank
  (:require [compojure.core :refer :all]
            [ring.util.response :refer [response]]
            [clojure.java.io :as io]))

(defn parse
    "Converts a list of pairs to rows of columns"
    [string]
    (map   #(clojure.string/split % #" ")
            (clojure.string/split string #"\n")))

(defn updateScoresFunction [m id k]
    (defn parent
        [id m]
        (first (first (filter #(contains? (second (second %)) id) m))))
    (defn incScore
        [[score set] key id k m]
        (if (contains? set id)
            [(+ score (reduce * 1 (take k (repeat 1/2)))) set]
            [score set]))
    (let [updated (into {} (for [[key value] m] [key (incScore value key id k m)]))]
        (if (= id "1")
            updated
            (recur updated (parent id updated) (inc k)))))

(defn foo
    "Recursively updates scores"
    [record id k]
    (if (= id "1")
        record
        (let [entry (first (filter #(contains? (:children %) id) record))
              index (.indexOf record entry)
              increment (reduce * 1 (take k (repeat 1/2)))]
            (recur (assoc record index (assoc entry :score (+ (:score entry) increment))) (:id entry) (inc k)))))

(defn process
    "Processes invitations made"
    [[[A B] & tail] record updateScores]
    (defn proceed
        [tail record updateScores]
        (if tail
            (process tail record updateScores)
            record))
    (defn checkB
        [record inviter B tail]
        (if (not (some #(= (:id %) B) record))
            (let [record (conj record {:id B :score -1 :children #{}})
                  record (assoc record (.indexOf record inviter) (assoc inviter :children (conj (:children inviter) B)))]
                 (proceed tail record updateScores))
            (proceed tail record updateScores)))
    (if-let [entry (first (filter #(= (:id %) A) record))]
        (if (= (:score entry) -1)
            (let [updatedEntry (assoc entry :score 0)
                  record (updateScores (assoc record (.indexOf record entry) updatedEntry) A 0)]
                (checkB record updatedEntry B tail))
            (checkB record entry B tail))
        (proceed tail record updateScores)))

(defn rank
    "Ranks invited people based on their score"
    [record]
    (defn normalize
        [])
    (let sorted (reduce #(conj % (select-keys %2 [:id :score])) [] record))
        )

(defn rank-endpoint [config]
    (let [input "resources/public/input.txt"
          record [{:id "1" :score 0 :children #{}}]]
        (routes
            (GET "/" [] (io/resource "public/index.html"))
            (GET "/rank" [] (response (trampoline process (parse (slurp input)) record foo))))))
