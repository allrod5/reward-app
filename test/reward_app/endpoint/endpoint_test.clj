(ns reward-app.endpoint.endpoint-test
  (:require [clojure.test :refer :all]
            [reward-app.endpoint.endpoint :as endpoint]))

(def handler
  (endpoint/endpoint {}))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))
