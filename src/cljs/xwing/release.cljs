(ns xwing.release 
  (:require [xwing.app :refer [mount!]]))

(defn init! []
  (set! *print-fn* (fn [& _])) ;; Ignore println statements in prod
  (mount!))
