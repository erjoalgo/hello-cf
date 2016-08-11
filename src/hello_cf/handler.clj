(ns hello-cf.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as r]
            [clojure.tools.logging :as log]
            [ring.adapter.jetty :refer [run-jetty]]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/counter" {session :session}
       (let [count   (:count session 0)]
         (-> (r/response
              (format "You accessed this page %d times (%s)"
                      count session))
             (assoc-in [:session :count] (inc count))
             (assoc-in [:headers "Content-Type"] "text/plain"))))
  (GET "/echo-ip" []
       ;;from
       ;;https://github.com/ring-clojure/ring/wiki/Concepts
       (fn [req]
       (log/infof "on echo ip: %s" req)
         {:status 200
          :headers {"Content-Type" "text/plain"}
          :body (:remote-addr req)}))

  (fn [req]
    ;echo env variables
    (when (.startsWith (:uri req) "/echo-env")
      (let [uri (:uri req)
            components (-> (clojure.string/split uri #"/") rest)
            env-val (reduce get (System/getenv) (rest components))]
        (log/infof "components: %s" components)
        (when env-val
          {:status 200
           :headers {"Content-Type" "text/plain"}
           :body (-> env-val pr-str)}))))

  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))

(defn -main [& args]
  (let [port (->
              (System/getenv)
              (get "PORT")
              (or (throw (Throwable. "must provide port number in env")))
              Integer/parseInt)]
    (log/infof "running in port %s" port)
    (run-jetty app {:port port})))
