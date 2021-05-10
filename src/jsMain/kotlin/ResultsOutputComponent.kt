import kotlinx.css.*
import react.*
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
                styledDiv {
                    css {
                        backgroundColor = Color.turquoise
                    }
                    +r.expr
                }
                styledDiv {
                    css {
                        backgroundColor = Color.lemonChiffon
                    }
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