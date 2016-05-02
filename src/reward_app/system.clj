(ns reward-app.system
  (:require [com.stuartsierra.component :as component]
            [duct.component.endpoint :refer [endpoint-component]]
            [duct.component.handler :refer [handler-component]]
            [duct.middleware.not-found :refer [wrap-not-found]]
            [meta-merge.core :refer [meta-merge]]
            [ring.component.jetty :refer [jetty-server]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [reward-app.endpoint.rank :refer [rank-endpoint]]
            [reward-app.endpoint.invite :refer [invite-endpoint]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]))

(def base-config
  {:app {:middleware [[wrap-not-found :not-found]
                      [wrap-json-body {:keywords? true}]
                      [wrap-json-response]
                      [wrap-defaults :defaults]]
         :not-found  "Resource Not Found"
         :defaults   (meta-merge api-defaults {})}})

(defn new-system [config]
  (let [config (meta-merge base-config config)]
    (-> (component/system-map
         :app  (handler-component (:app config))
         :http (jetty-server (:http config))
         :rank (endpoint-component rank-endpoint)
         :invite (endpoint-component invite-endpoint))
        (component/system-using
         {:http [:app]
          :app  [:rank :invite]
          :rank []
          :invite []}))))
