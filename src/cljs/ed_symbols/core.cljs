(ns ed-symbols.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   )
  )

(def symbols ["fire" "heart" "cloud" "leaf" "star" "send" "th-large"])
(def colors ["orange" "green" "violet" "red" "blue" "DarkTurquoise" "black"])

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

(defn log [s] (.log js/console s))

(def data (atom {:mode :ask  ;; :ask :answer
                 :question 0
                 :questions []
                 :results []
                 :word []
                 }) )

(defn build-questions []
  (let [w-len (count (:word @data))]
    (vec (take w-len
               (shuffle
                (for [x (range w-len) y [:color :symbol]] [x y]))))
    )
)

(defn question-color? [question]
  (= :color (question 1))
)

(defn correct-answer? [answer]
  (let [d @data
        word (:word d)
        [pos type] ((:questions d)
                   (:question d))
        letter (word pos)
        ]
    (log (str answer " -- " letter))
    (= answer (letter (if (= type :symbol) 0 1)))
    )
)

(defn add-result [r]
  (let [res (:results @data)
        w-len (count (:word @data))]
    (log (str "add result " r))
    (swap! data assoc :results (conj res [w-len r])))
)

(defn next-word
  ([] (next-word (count (:word @data))))
  ([n]
     (log "next word")
     (swap! data assoc :mode :ask)
     (swap! data assoc :word (symbol-color-word n))
     (swap! data assoc :questions (build-questions))
     (swap! data assoc :question 0)
     )
  )

(defn on-correct-answer []
  (let [d @data
        next-pos (inc (:question d))
        questions (:questions d)
        last (= next-pos (count questions))]
    (if last
      (do (add-result true) (next-word))
      (swap! data assoc :question next-pos))
    )
)

(defn do-answer [answer]
  (if (correct-answer? answer)
    (on-correct-answer)
    (do (add-result false) (next-word))
    )
  )

(defn ask-questions []
  (swap! data assoc :mode :answer)
)

;;css stuff
(defn css
  ([css-map] css-map)
  ([css-map css-map2] (merge css-map css-map2))
)
(def css-border {:border "1px solid gray"})
(def css-element (css {:padding "10px"
             :margin "3px"} {}))


(defn colors-component []
  [:div
   (map (fn [x]
          [:span.glyphicon
           {:key x
            :on-click #(do-answer x)
            :style
            (css css-element
                 {:background-color x :padding "25px"})}
           ]) colors)
   ]
  )

(defn symbols-component []
  [:div {:style {:font-size "30px"}}
   (map (fn [x]
          [:span.glyphicon
           {:class (str "glyphicon-" x)
            :on-click #(do-answer x)
            :key x
            :style css-element
            }]) symbols)
   ]

  )

(defn word-component []
  (let [d @data
        mark (= :answer (:mode d))
        [q-pos q-type] ((:questions d) (:question d))
        ]
    [:div {:style {:font-size "50px"}}
     (map (fn [i [s c] v]
            (if (and (= i q-pos) mark)
              [:span.glyphicon.glyphicon-question-sign
               {:style css-element}]
              [:span.glyphicon
               {:class (str "glyphicon-" s)
                :style (css css-element {:color c})}]
              )
            )
          (range 10) (:word @data) )
     ])
  )

(defn change-size-component []
  (let [size (count (:word @data))]
    [:span
     [:button.btn.btn-default
      {:on-click #(next-word (dec size))
       :disabled (<= size 1)}
      [:span.glyphicon.glyphicon-minus]]
     [:button.btn.btn-default
      {:on-click #(next-word (inc size))
       :disabled (>= size 8)}
      [:span.glyphicon.glyphicon-plus]]]
    ))

(defn visualized-component []
  [:button.btn.btn-success
   {:on-click ask-questions
    :style {:font-size "40px"
            :padding "0px 30px"
            :margin "10px"}}
   [:span.glyphicon.glyphicon-ok]])

(defn results-component []
  [:div
   [:span.glyphicon.glyphicon-eye-open {:style {:margin-right "5px"}}]
   (map (fn [[n v] x]
          (if v
             [:span.glyphicon.glyphicon-ok {:style {:color "green"}}]
             [:span.glyphicon.glyphicon-remove {:style {:color "red"}}]
            )) (:results @data))
   ]
  )

(defn question-component []
  [:div {:style {:margin "20px"}}
   (let [data @data
         question ((:questions data)
                   (:question data)
                   )]
     (if (question-color? question)
       [colors-component]
       [symbols-component]))]
  )

(defn ask-component []
  [:div  {:style {:font-size "50px"
                  :margin "20px"}}
   [change-size-component]
   [visualized-component]])

(defn app-component []
  [:div
   [results-component]
   [:div {:style {:text-align "center"}}
    [word-component]
    (if (= :ask (:mode @data))
      [ask-component]
      [question-component])
    ]]
)

(defn render []
  (reagent/render-component
   [app-component]
   (.getElementById js/document "app")
   )
  )

(defn ^:export run []
  "initialize stuff"
  (log "let's go!!")
  (next-word 3)
  (render)
  )
