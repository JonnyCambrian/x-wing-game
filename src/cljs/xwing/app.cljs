(ns xwing.app 
  (:require [reagent.core :as r :refer [create-compiler]]
            [reagent.dom :refer [render]]
            [xwing.components.board :refer [game]]
            ))

(defonce functional-compiler (create-compiler {:function-components true}))

(defn app []
  [:div [game]])

(defn mount! []
  (render [app] (.getElementById js/document "root") functional-compiler))
