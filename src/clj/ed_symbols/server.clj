(ns ed-symbols.server
  (:require
   [compojure.route :refer [resources]]
   [ring.adapter.jetty :refer [run-jetty]])
)

(defn run [& args]
  (run-jetty (resources "/") {:port 3000
                              :join? false}))
