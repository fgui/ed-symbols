(ns ed-symbols.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   )
  )

(def symbols ["fire" "heart" "cloud" "leaf" "star" "send" "th-large"])
(def colors ["orange" "green" "violet" "red" "blue" "cyan" "black"])

;; create lists of at least 9 elements
(defn color-list [] (into (shuffle colors) (shuffle colors)))
(defn symbol-list [] (into (shuffle symbols) (shuffle symbols)))

(defn symbol-color-word [size]
  (vec (map
        (fn [a b] [a b])
        ;;shuffle colors to avoid pattern
        (shuffle (take size (symbol-list)))
        (shuffle (take size (color-list))))
       )
  )

(def data (atom {:mode :show-word  ;; :show-word :question
                 :question 0
                 :questions []
                 :results []
                 :word
                 (symbol-color-word 3)}) )

;;css stuff
(defn css
  ([css-map] css-map)
  ([css-map css-map2] (merge css-map css-map2))
)
(def css-border {:border "1px solid gray"})
(def css-element (css {:padding "10px"
             :margin "3px"} css-border))


(defn colors-component []
  [:div
   (map (fn [x]
          [:span.glyphicon
           {:style
            (css css-element
                 {:background-color x :padding "20px"})}
           ]) colors)
   ]
  )

(defn symbols-component []
  [:div {:style {:font-size "20px"}}
   (map (fn [x]
          [:span.glyphicon
           {:class (str "glyphicon-" x)
            :style css-element
            }]) symbols)
   ]

  )

(defn word-component []
  [:div {:style {:font-size "50px"}}
   (map (fn [[s c] v]
          [:span.glyphicon
           {:class (str "glyphicon-" s)
            :style (css css-element {:color c})}]
          )
        (:word @data) )
   ]
  )

(defn set-size [s]
  (swap! data assoc :word (symbol-color-word s))
  )

(defn change-size-component []
  (let [size (count (:word @data))]
    [:span
     [:button.btn.btn-default
      {:on-click #(set-size (dec size))
       :disabled (<= size 2)}
      [:span.glyphicon.glyphicon-minus]]
     [:button.btn.btn-default
      {:on-click #(set-size (inc size))
       :disabled (>= size 8)}
      [:span.glyphicon.glyphicon-plus]]]
    ))

(defn visualized-component []
  [:button.btn.btn-success
   {:style {:font-size "40px"
            :padding "0px 30px"
            :margin "10px"}}
   [:span.glyphicon.glyphicon-eye-open]])


(defn render []
  (reagent/render-component
   [:div {:style {:text-align "center"}}
    [word-component]
    [:div  {:style {:font-size "50px"
                    :margin "20px"}}
     [change-size-component]
     [visualized-component]]
    [colors-component]
    [symbols-component]
    ]
   (.getElementById js/document "app")
   )
  )

(defn ^:export run []
  "initialize stuff"
  (.log js/console "let's go!!")
  (render)
  )
