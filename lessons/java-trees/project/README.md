# Trees

> **New to an idea here?** Press the **Dozer** button in the top bar. His guides explain each idea this lesson uses, and **I'm so lost → Teach me, step by step** walks you through every job.

More depth: the team docs page [Trees](https://frc-team-4143.github.io/docs/software/java/advanced/trees).

A list is a line of things, one after another. A **tree** branches: each
item (called a **node**) can point at other nodes below it, called its
**children**. The node at the very top is the **root**, and a node with no
children is a **leaf**. (Computer trees grow upside down.)

In this lesson you build two trees:

- a **binary search tree** of team numbers. "Binary" means each node has at
  most two children, a left one and a right one. "Search" means they follow
  a rule: smaller numbers go left, bigger ones go right. That rule lets you
  find a team by looking down just one path instead of checking every team.
- an **auto routine** shaped like the behavior trees the robot runs, where a
  node can have any number of children.

Trees and recursion go together. Each child is the top of a smaller tree, so
a method can handle one node and then call itself on the children. The
**base case** is the bottom: a `null` link, or a node with no children.

## What you need to do

`src/Main.java` is already written, so click **Run** any time. `TeamNode`
(`src/TeamNode.java`) is done too: it holds a `team_` number and two links,
`left_` and `right_`, that are `null` when there's nothing there. Team
standard: fields use `trailing_snake_case_`.

In `src/TeamTree.java`, `root_` is the top node (`null` while the tree is
empty). Each public method can call a **helper method** you write, one that
takes a `TeamNode` and calls itself on `node.left_` and `node.right_`.

1. **Insert and find.**
   - `insert(int team)`: walk down from the root, going left when `team` is
     smaller than the node's team and right when it's bigger, until you hit
     an empty (`null`) spot. Put a new `TeamNode` there. If the team is
     already in the tree, don't add it again.
   - `contains(int team)`: `true` if the team is in the tree. Go down only
     the side it could be on.
   - `size()`: how many teams are in the tree.
2. **In order.** `inOrder()` returns an `ArrayList<Integer>` of every team,
   lowest first. Visit the left side, then the node, then the right side.
   Because of the left/right rule, that comes out sorted.
3. **Height.** `height()` counts the nodes on the longest path from the root
   to the bottom. An empty tree has height `0`, a tree with one team has
   height `1`.
4. **An auto tree** (`src/AutoNode.java`). Each `AutoNode` has a name and a
   list of `children_`. A node with children is a group, like a Sequence.
   A node with no children is an **action**, like `"shoot"`.
   - `countActions()`: how many actions are in this tree. A node with no
     children counts as 1.
   - `depth()`: the number of nodes on the longest path from this node down.
     An action on its own has depth `1`.
5. **Run it.** `Main` puts nine scouted teams into the tree (one is a
   repeat), lists them in order, and measures a two-piece auto.

### New Java in this lesson

- a class whose fields are the **same type as the class**: `TeamNode left_`
  inside `TeamNode`. That's what links nodes into a tree.
- `null` as "nothing here", and checking `node == null` before you use a node
- **helper methods** marked `private`, used only inside their own class
- recursion that calls itself **twice**, once for each side
- `Math.max(a, b)`, the bigger of two numbers

## Why this matters on the robot

Our autos are **behavior trees**, built in Arbor. A Sequence node runs its
children one after another, a Parallel node runs them together, and the
leaves are the actions that move the robot. Counting actions and measuring
depth are the same walks the robot does when it runs one. Search trees show
up behind the scenes too: Java's `TreeMap`, which keeps a map sorted by its
keys, is a binary search tree inside.

## Checking your work

Click **Checkpoints** in the top bar and run **Verify** whenever you want.
