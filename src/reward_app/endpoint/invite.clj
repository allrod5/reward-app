(ns reward-app.endpoint.invite
  (:require [compojure.core :refer :all]
            [ring.util.response :refer [response]]))

(defn invite-endpoint [config]
    (routes
        (GET "/invite" [] (response {:hello "world"}))))
