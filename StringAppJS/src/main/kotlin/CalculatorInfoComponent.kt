import calculator.StringCalculatorApp
import kotlinx.css.*
import react.*
import styled.*
import react.dom.*


class CalculatorInfoComponent : RComponent<CalculatorProps, RState>() {
    override fun RBuilder.render() {
        h1 { +"Base Information" }
        for (s in StringCalculatorApp.helloInfo.split('\n')) {
            styledP {
                css {
                    marginBottom = 0.px
                    marginTop = 3.px
                }
                +s
            }
        }

        h1 { +"System Operations" }
        styledUl {
            css {
                whiteSpace = WhiteSpace.pre
            }
            for (f in props.calcApp.process(
                StringCalculatorApp.startOfControlSequence +
                        StringCalculatorApp.ControlSeq.SystemOperations.v
            ).split('\n').filterNot { it.isBlank() }) {
                li { +f }
            }
        }

        h1 { +"System Functions" }
        styledUl {
            css {
                whiteSpace = WhiteSpace.pre
            }
            for (f in props.calcApp.process(
                StringCalculatorApp.startOfControlSequence +
                        StringCalculatorApp.ControlSeq.SystemFunctions.v
            ).split('\n').filterNot { it.isBlank() }) {
                li { +f }
            }
        }
    }


}

fun RBuilder.calculatorInfo(handler: CalculatorProps.() -> Unit): ReactElement {
    return child(CalculatorInfoComponent::class) {
        this.attrs(handler)
    }
}