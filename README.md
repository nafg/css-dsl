# `css-dsl`: A DSL for CSS Frameworks
Instead of stringly typed, noisy code like this,

```scala
<.div(
  ^.cls := ("panel hidden-xs panel-" + (if (success) "success" else "default")),
  <.div(
    ^.cls := "panel-heading",
    <.h3(^.cls := "panel-title", "Panel title")
  )
)
```

write code like this:

```scala
<.div.panel.hiddenXs(
  if (success) C.panelSuccess else C.panelDefault,
  <.div.panelHeading(
    <.h3.panelTitle("Panel title")  
  )
)
```

## Variants
### CSS Frameworks
 * Bootstrap 3
 * Bootstrap 4
 * Bulma
 * Semantic UI

### Targeted Libraries
 * Scalajs-react
 * Scalatags (currently only the Text bundle for JVM)

### DSL Flavors
 * As chainable extension methods on tags
 * As methods on the `C` object

Additionally, most frameworks are available with prefixed and unprefixed methods


## Usage

### Dependency Coordinates
#### Resolver
Artifacts are published to Bintray and synced to Bintray JCenter. For SBT use `resolvers += Resolver.jcenterRepo` or `useJCenter := true` (prefixed with `ThisBuild / ` if needed). For other build tools add https://jcenter.bintray.com as a maven repository.

#### Artifact

| CSS Framework | Library                  | SBT module ID                                                    |
|---------------|--------------------------|------------------------------------------------------------------|
| Bootstrap 3   | `scalatags.Text` (JVM)   | "io.github.nafg.css-dsl" %% "bootstrap3_scalatags" % "0.5.0"     |
| Bootstrap 3   | scalajs-react (scala.js) | "io.github.nafg.css-dsl" %%% "bootstrap3_scalajsreact" % "0.5.0" |
| Bootstrap 4   | `scalatags.Text` (JVM)   | "io.github.nafg.css-dsl" %% "bootstrap4_scalatags" % "0.5.0"     |
| Bootstrap 4   | scalajs-react (scala.js) | "io.github.nafg.css-dsl" %%% "bootstrap4_scalajsreact" % "0.5.0" |
| Bulma         | `scalatags.Text` (JVM)   | "io.github.nafg.css-dsl" %% "bulma_scalatags" % "0.5.0"          |
| Bulma         | scalajs-react (scala.js) | "io.github.nafg.css-dsl" %%% "bulma_scalajsreact" % "0.5.0"      |
| Semantic UI   | `scalatags.Text` (JVM)   | "io.github.nafg.css-dsl" %% "semanticui_scalatags" % "0.5.0"     |
| Semantic UI   | scalajs-react (scala.js) | "io.github.nafg.css-dsl" %%% "semanticui_scalajsreact" % "0.5.0" |


### Import

| Framework   | Prefix | Import                              |
|-------------|--------|-------------------------------------|
| Bootstrap 3 | None   | `import cssdsl.bootstrap3.Dsl._`    |
| Bootstrap 3 | `bs`   | `import cssdsl.bootstrap3.BsDsl._`  |
| Bootstrap 3 | `bs3`  | `import cssdsl.bootstrap3.Bs3Dsl._` |
| Bootstrap 4 | None   | `import cssdsl.bootstrap4.Dsl._`    |
| Bootstrap 4 | `bs`   | `import cssdsl.bootstrap4.BsDsl._`  |
| Bootstrap 4 | `bs4`  | `import cssdsl.bootstrap4.Bs4Dsl._` |
| Bulma       | None   | `import cssdsl.bulma.Dsl._`         |
| Bulma       | `b`    | `import cssdsl.bulma.BDsl._`        |
| Semantic UI | `s`    | `import cssdsl.semanticui.SDsl._`   |

### Code

The import gives you two things:
 
1. Chainable extension methods on the target library's tag type (scalatags `ConcreteHtmlTag[String]` or scalajs-react's `TagOf[Node]`). These methods return a modified version of the tag which allows you to chain them, and then continue as usual (for instance `apply`ing modifiers and content to it).
2. The `C` object, which methods with the same name but that return a class modifier directly (scalatags `Modifier` or scalajs-react `TagMod`). This allows you to use classes conditionally.

For an example illustrating both see the second snippet at the top of this file.

If you use a prefixed flavor the method names are the same except they start with the chosen prefix, and the first letter after the prefix is capitalized. So for example `bootstrap4.Dsl` will use `tableHover` while `bootstrap4.BsDsl` will use `bsTableHover`.

## Contributing

The DSLs are generated using [ph-css](https://github.com/phax/ph-css) and [Scalameta](https://scalameta.org/).
The CSS is read from a CDN and parsed, class selectors are extracted, and their names are converted to camel case.
 
If you want to add or update a CSS framework you just have to update `build.sbt`.

To add a new target library you first have to implement it in [project/TargetImpl.scala](project/TargetImpl.scala).
