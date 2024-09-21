(ns no.olavfosse.malt
  ;; - [x] Make the plots from chapter one
  (:require [nextjournal.clerk.viewer :as clv]
            [nextjournal.clerk :as cle]
            [clojure.core :as cc])
  (:import java.awt.image.BufferedImage))

(defprotocol Arithmetic
  (+ [a b])
  (- [a b])
  (* [a b])
  (/ [a b]))

(let [ccops {:+ (fn [a b] (if (number? b)
                            (cc/+ a b)
                            (+ b a)))
             :- cc/-
             :* (fn [a b] (if (number? b)
                            (cc/* a b)
                            (* b a)))
             :/ cc//}]
  (extend clojure.lang.Numbers
    Arithmetic
    ccops)
  (extend Number
    Arithmetic
    ccops))

(+ 1 2 )

;; # Data structures
;; For now we're merely using vectors as tensors, but I think that
;; might be too slow in the long run.












(defn line [x] ;; x is the formal
  (fn [θ] ;; θ is the parameters
    (+ (* (first θ) x) (second θ))))

(declare sqr)
(defn quad [t]
  (fn [θ]
    (+ (* (first θ) (sqr t))
       (+ (* (second θ) t) (nth θ 2)))))

(defn sum [xs]
  (transduce identity + xs))

(defn sqr [x]
  (* x x))

(defn l2-loss [target]
  (fn [xs ys]
    (fn [θ]
      (let [pred-ys ((target xs) θ)]
        (sum (sqr (- ys pred-ys)))))))

(defn revise [f revs θ]
  (if
    (zero? revs) θ
    (revise f (dec revs) (f θ))))

(defn scalar? [x]
  (or (number? x) #_(dual? x)))

(defn shape [t]
  (if (scalar? t)
    (list)
    (conj (shape (first t)) (count t))))

(defn rank [t]
  (count (shape t)))

(defn sum¹ [t]
  (if (= (rank t) 1)
    (reduce + t)
    (mapv sum¹ t)))

(def ^:dynamic revs)
(def ^:dynamic α)


;; ===========================
;;       Visualization &
;;         Tangibility
;; ===========================

(defn unit-lengths [{:as spec
                     :keys [width height]
                     [[x-min x-max] [y-min y-max]] :range}]
  (let [x-units (- x-max x-min)
        y-units (- y-max y-min)]
    [(/ width x-units) (/ height y-units)]))

(defn origin-offset [{:as spec
                      :keys [height]
                      [[x-min x-max] [y-min y-max]] :range}]
  (let [[x-unit y-unit] (unit-lengths spec)]
    [(* x-unit (- 0 x-min)) (+ height (* y-unit y-min))]))

(defn- border? [{:as spec :keys [width height]} x y]
  (or (#{0 (dec width)} x) (#{0 (dec height)} y)))

(defn- axis? [{:as spec :keys [width height]} x y]
  (let [[x-offset y-offset] (origin-offset spec)]
    (or (= x x-offset) (= y y-offset))))

(defn axes-grid [{:as spec :keys [width height unit]}]
  (let [img (BufferedImage. width height BufferedImage/TYPE_INT_RGB)]
    (dotimes [x width]
      (dotimes [y height]
        (.setRGB img x y
                 (cond
                   (axis? spec x y) 0xFF
                   :else 0xF0F0F0 #_(+ 0xFFFFFF (* 10 x) (* 10 y))))))
    img))

(defn draw-border [img {:as spec :keys [width height unit]}]
  (dotimes [x width]
    (dotimes [y height]
      (when (border? spec x y)
        (.setRGB img x y 0x00)))))

(defn set-rgb
  "Like .setRGB but tolerates out of bounds writes"
  [img x y rgb]
  (when (and (>= x 0) (< x (.getWidth img))
             (>= y 0) (< y (.getHeight img)))
    (.setRGB img x y rgb)))

(defn project [{:as spec
                :keys [width height]
                [x-range y-range] :range}]
  (let [[x-unit y-unit] (unit-lengths spec)
        [x-offset y-offset] (origin-offset spec)]
    (fn [[x y]]
      [(+ x-offset (* x x-unit)) (- y-offset (* y y-unit))])))

(defn normalize-features [spec]
  (-> spec
      (dissoc :f)
      (dissoc :points)
      (update :features #(cond-> %
                           (:f spec) (conj (:f spec))
                           (:points spec) (conj (:points spec))))))

(defn dwim
  "Takes a vague plot spec from the client and transforms it into a
  explicit plot spec for the internal system. Tries to be smart about
  it."
  [spec]
  (-> (merge {:range [[-6 6] [-6 6]]
              :width 500
              :height 500}
             (cond (fn? spec) {:f spec}
                   :else spec))
      normalize-features))

;; Glossary
;; ---------
;; Offset   The coordinate of a pixel in a raster





(defn draw-dot [img x y]
  (set-rgb img x (dec y) 0)
  (set-rgb img (dec x) y 0)
  (set-rgb img x y 0)
  (set-rgb img (inc x) y 0)
  (set-rgb img x (inc y) 0))

(defn draw-features [img {:as spec
                          :keys [features]
                          [[x-min x-max]] :range}]
  (doseq [f features]
    (cond
      (fn? f) (doseq [[x y] (->> (range x-min x-max 0.001)
                                 (map (juxt identity f))
                                 (map (project spec)))]
                (set-rgb img x y  0xFF00))
      (seqable? f) (transduce (map (project spec))
                              (completing (fn [_ [x y]] (draw-dot img x y)))
                              nil
                              f))))

(defn plot [spec]
  (let [spec (dwim spec)]
    (doto (axes-grid spec)
      (draw-features spec)
      (draw-border spec))))



(fn [θ] )
