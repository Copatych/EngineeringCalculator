import react.*
import react.dom.*
import kotlinx.css.*
import kotlinx.html.*
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onSubmitFunction
import org.w3c.dom.events.Event
import styled.*

external interface InputProps : RProps {
    var onSubmitFunction: (Event) -> Unit
    var onChangeFunction: (Event) -> Unit
}

class InputComponent : RComponent<InputProps, RState>() {
    override fun RBuilder.render() {
        form {
            attrs {
                onSubmitFunction = props.onSubmitFunction
            }
            styledInput(type = InputType.text) {
                css {
                    width = 100.pct
                    height = 35.px
                }
                attrs {
                    id = "inputExpr"
                    placeholder = "Print expression"
                    onChangeFunction = props.onChangeFunction
                }
            }
        }
    }
}


fun RBuilder.input(handler: InputProps.() -> Unit): ReactElement {
    return child(InputComponent::class) {
        this.attrs(handler)
    }
}