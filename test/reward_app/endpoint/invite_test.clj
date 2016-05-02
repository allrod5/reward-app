(ns reward-app.endpoint.invite-test
  (:require [clojure.test :refer :all]
            [reward-app.endpoint.invite :as invite]))

(def handler
  (invite/invite-endpoint {}))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))
