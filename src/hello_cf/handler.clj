(ns hello-cf.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clojure.tools.logging :as log]
            [ring.adapter.jetty :refer [run-jetty]]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))

(defn -main [& args]
  (let [port (->
              (System/getenv)
              (get "PORT")
              (or (throw (Throwable. "must provide port number in env")))
              Integer/parseInt)]
    (printf "running in port %s\n" port)
    (log/warnf "running in port %s\n" port)
    (run-jetty app {:port port})))
