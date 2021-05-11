import kotlinx.css.*
import kotlinx.html.*
import react.*
import react.dom.*
import styled.*

class ResultsOutputComponent : RComponent<ResultsProps, RState>() {
    override fun RBuilder.render() {
        for (r in props.outputResults.reversed()) {
            styledDiv {
                css {
                    borderStyle = BorderStyle.solid
                    borderWidth = 1.px
                    width = 100.pct
                    marginTop = 3.px
                }
                attrs {
                    classes = setOf("resultsOutput")
                }
                div("exprOutput") {
                    +r.expr
                }
                div("resOutput") {
                    +r.res
                }
            }
        }
    }
}

fun RBuilder.resultsOutput(handler: ResultsProps.() -> Unit): ReactElement {
    return child(ResultsOutputComponent::class) {
        this.attrs(handler)
    }
}