(ns reward-app.endpoint.rank-test
  (:require [clojure.test :refer :all]
            [reward-app.endpoint.rank :as rank]))

(def handler
  (rank/rank-endpoint {}))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))
