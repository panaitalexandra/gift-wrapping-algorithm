# gift-wrapping-algorithm
# Convex Hull Determination (Jarvis March / Gift Wrapping)

## 1. Requirement

Given a set of **N** distinct points in a 2D plane, the objective is to find the **Convex Hull**—the smallest convex polygon that contains all the points, either inside it or on its boundary.

The solution implements the **Jarvis March algorithm**, also known as **Gift Wrapping**, which incrementally builds the hull by selecting the outermost points based on geometric orientation.



---

## 2. Algorithm Description

The algorithm constructs the convex polygon by iteratively selecting points that form the exterior edges, using the determinant to calculate the orientation of point triplets.

### Phase I: Pre-processing
* All points are stored in a list and sorted in ascending order by their **X-coordinates** (abscissa).
* In case of ties (same X), the **Y-coordinate** (ordinate) is used for tie-breaking.
* Two key points are identified:
    * **$P_{start}$**: The point with the minimum X-coordinate.
    * **$P_{end}$**: The point with the maximum X-coordinate.

### Phase II: Construction of the Hull
The algorithm divides the construction into two distinct steps to form the complete polygon:

#### A. Upper Hull Construction
Starting from $P_{start}$ and moving towards $P_{end}$, the algorithm searches for the next candidate point $P_{candidate}$ such that all other points in the set lie to the right of the oriented segment ($P_{current}$, $P_{candidate}$).

The validation is based on the **sign of the determinant**:
$$\text{det}(P_{current}, P_{candidate}, P_{test}) > 0$$

#### B. Lower Hull Construction
The process is repeated starting again from $P_{start}$ towards $P_{end}$, but this time searching for points that form the lower boundary of the hull. The orientation condition is reversed:
$$\text{det}(P_{current}, P_{candidate}, P_{test}) < 0$$

---

## 3. Complexity
* **Time Complexity:** $O(n \cdot h)$, where $n$ is the total number of points and $h$ is the number of points on the convex hull. In the worst case (all points are on the hull), the complexity is $O(n^2)$.
* **Space Complexity:** $O(n)$ to store the initial points and $O(h)$ for the resulting hull vertices.
