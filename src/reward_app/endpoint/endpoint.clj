(ns reward-app.endpoint.endpoint
  (:require [compojure.core :refer :all]
            [ring.util.response :refer [response redirect]]
            [clojure.java.io :as io]
            [reward-app.controller :as controller])
    (:use clojure.pprint))

(defn endpoint [config]
    (let [input "resources/public/input.txt"]
        (def record (atom (controller/loadFile input)))
        (routes
            (GET "/" [] (io/resource "public/index.html"))
            (GET "/rank" [] (response (controller/rank @record)))
            (POST "/invite/:inviter/:invited" [inviter invited]
                (swap! record controller/invite (str inviter) (str invited))
                (response "Invite received!")))))
