import calculator.StringCalculatorApp
import react.*
import kotlinx.css.*
import kotlinx.html.*
import org.w3c.dom.HTMLInputElement
import styled.*

data class PrintedResult(val expr: String, val res: String)

external interface AppState : RState {
    var inputText: String
}

external interface CalculatorProps : RProps {
    var calcApp: StringCalculatorApp
}

external interface ResultsProps : RProps {
    var outputResults: MutableList<PrintedResult>
}

external interface AppProps : CalculatorProps, ResultsProps

class App : RComponent<AppProps, AppState>() {

    override fun AppState.init() {
        inputText = ""
    }

    private val widthBaseDiv = 50.vw
    private val widthCalculatorInfo = 40.vw

    override fun RBuilder.render() {
        styledDiv {
            css {
                position = Position.absolute
                top = 25.px
                right = 25.px
                width = widthCalculatorInfo
            }
            attrs {
                id = "calculatorInfo"
            }
            calculatorInfo { calcApp = props.calcApp }
        }
        styledDiv {
            css {
                position = Position.absolute
                top = 25.px
                left = 25.px
                width = widthBaseDiv
            }
            attrs {
                id = "calculatorBaseDiv"
            }
            input {
                onSubmitFunction = {
                    setState {
                        val outputText = try {
                            props.calcApp.process(inputText)
                        } catch (e: Exception) { // TODO My Exceptions
                            e.message.toString()
                        }
                        props.outputResults += PrintedResult(inputText, outputText)
                    }
                    it.preventDefault()
                }
                onChangeFunction = {
                    val target = it.target as HTMLInputElement
                    state.inputText = target.value
                }
            }
            resultsOutput {
                outputResults = props.outputResults
            }
        }
    }
}


fun RBuilder.app(handler: AppProps.() -> Unit): ReactElement {
    return child(App::class) {
        this.attrs(handler)
    }
}