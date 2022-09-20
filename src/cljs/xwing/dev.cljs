(ns xwing.dev 
  (:require [xwing.app :refer [mount!]]
            [devtools.core :as devtools]))

(defn ^:dev/after-load reload []
  (mount!))

(defn init! []
  (enable-console-print!)
  (devtools/install!)
  (mount!))
