(ns xwing.components.board
  (:require ["react" :as react]
            ["tabler-icons-react" :as icon :refer [ArrowBack ArrowBearLeft
                                                   ArrowBearRight ArrowNarrowLeft
                                                   ArrowNarrowRight ArrowNarrowUp]]
            [reagent.core :as r]
            [xwing.game.data :refer [PILOTS SHIPS]]
            [xwing.game.main :refer [action init]]))

(defonce state (r/atom (init)))

(defn action! [args]
  (println args)
  (swap! state action args))

(defn ship-pane [{:keys [name pilot damage shield in-range ship-path prompt attack-res destroyed?]}]
  (let [ship-def (get-in SHIPS [(get-in PILOTS [pilot :ship])])]
    [:div.ship-pane {:key name
                     :class (when destroyed? "destroyed")}
     [:img {:class "pilot-img"
            :src (str "/img/pilots/" (-> (. name toLowerCase)
                                         (. replace " " "-"))
                      ".png")}]
     [:div "Shield: " shield "/" (:shield ship-def)]
     [:div "Damage: " damage "/" (:hull ship-def)]
     [:div (map (fn [{:keys [range pilot target]}]
                  [:div {:key target} [:b pilot] " in range #" range
                   [:button {:key target
                             :disabled (not (:attack prompt))
                             :on-click #(action! {:type :attack
                                                  :ship-path ship-path
                                                  :target target})}
                    "Attack"]])
                in-range)]
     (when (:attack-res prompt)
       [:div
        [:div "Attack rolls: " (str (:attack attack-res))]
        [:div "Defense rolls: " (str (:defense attack-res))]
        [:div "Damage: " (:damage attack-res)]
        [:button {:on-click #(action! {:type :attack-res
                                       :ship-path ship-path})}
         "Ok"]])]))

(def move-icon-size 26)

(def move-icons {:straight-1   {:x 2 :y 1 :icon ArrowNarrowUp}
                 :straight-2   {:x 2 :y 2 :icon ArrowNarrowUp}
                 :straight-3   {:x 2 :y 3 :icon ArrowNarrowUp}
                 :straight-4   {:x 2 :y 4 :icon ArrowNarrowUp}
                 :straight-5   {:x 2 :y 5 :icon ArrowNarrowUp}
                 :loop-1       {:x 6 :y 1 :icon ArrowBack}
                 :loop-2       {:x 6 :y 2 :icon ArrowBack}
                 :loop-3       {:x 6 :y 3 :icon ArrowBack}
                 :loop-4       {:x 6 :y 4 :icon ArrowBack}
                 :loop-5       {:x 6 :y 5 :icon ArrowBack}
                 :hard-left-1  {:x 0 :y 1 :icon ArrowNarrowLeft}
                 :hard-left-2  {:x 0 :y 2 :icon ArrowNarrowLeft}
                 :hard-left-3  {:x 0 :y 3 :icon ArrowNarrowLeft}
                 :soft-left-1  {:x 1 :y 1 :icon ArrowBearLeft}
                 :soft-left-2  {:x 1 :y 2 :icon ArrowBearLeft}
                 :soft-left-3  {:x 1 :y 3 :icon ArrowBearLeft}
                 :soft-right-1 {:x 3 :y 1 :icon ArrowBearRight}
                 :soft-right-2 {:x 3 :y 2 :icon ArrowBearRight}
                 :soft-right-3 {:x 3 :y 3 :icon ArrowBearRight}
                 :hard-right-1 {:x 4 :y 1 :icon ArrowNarrowRight}
                 :hard-right-2 {:x 4 :y 2 :icon ArrowNarrowRight}
                 :hard-right-3 {:x 4 :y 3 :icon ArrowNarrowRight}})

(def icon-colors {:green "lawngreen"
                  :red "crimson"})

(defn move-icon [{:keys [move color disabled]}]
  (let [{icon :icon x :x y :y} (get move-icons move)]
    [:div {:key move
           :style {:position "absolute"
                   :left (+ 26 (* x move-icon-size))
                   :bottom (- (* y move-icon-size) 2)
                   :color (get icon-colors color)}}
     [icon {:size 24 :color (get icon-colors color)}]]))

(defn ship [{:keys [x y orientation name moves ship-path prompt destroyed?]}]
  (let [[show-menu set-show-menu] (react/useState false)]
    (when (not destroyed?)
      [:div.ship {:key name
                  :class (when prompt "prompt")
                  :style {:left (* (- x 2) 10)
                          :bottom (* (- y 2) 10)}
                  :on-click #(set-show-menu (not show-menu))}
       [:div.ship-tile {:style {:transform (str "rotate(" orientation "deg)")}}]
       [:div.move-menu {:style {:display (if show-menu "block" "none")}}
        (map (fn [x]
               [:div {:key (keyword (str "index" x))
                      :style {:position "absolute"
                              :left 8
                              :bottom (+ 4 (* x move-icon-size))}} x])
             (range 6))
        (map (fn [{:keys [move] :as m}]
               [:div {:key move
                      :on-click #(action! {:type :move
                                           :choice move
                                           :ship-path ship-path})}
                [move-icon m]])
             moves)]
       [:div.name name]])))

(defn board [state]
  [:div
   (when (= :winner (get-in state [:phase :type]))
     [:div.alert
      (str "Player #" (get-in state [:phase :player]) " won!")])
   [:div.board
    (map ship (get-in state [:players 0 :ships]))
    (map ship (get-in state [:players 1 :ships]))]])

(defn game []
  [:div
   [:div.ship-menus
    [:div (map ship-pane (get-in @state [:players 0 :ships]))]
    [:div (map ship-pane (get-in @state [:players 1 :ships]))]]
   [board @state]])
