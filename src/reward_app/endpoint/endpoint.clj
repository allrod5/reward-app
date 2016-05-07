(ns reward-app.endpoint.endpoint
  (:require [compojure.core :refer :all]
            [ring.util.response :refer [response redirect]]
            [clojure.java.io :as io]
            [reward-app.controller :as controller])
    (:use clojure.pprint))

(defn- router [record break]
    (pprint record)
    (println break)
    (if break
        (redirect "/success")
        (routes
            (GET "/" [] (io/resource "public/index.html"))
            (GET "/rank" [] (response (controller/rank record)))
            (POST "/invite" [] (router (conj record {:id "42" :score 42 :children #{}}) true))
            (GET "/success" [] (io/resource "public/success.html")))))

(defn endpoint [config]
    (let [input "resources/public/input.txt"]
        (router (controller/loadFile input) false)))
