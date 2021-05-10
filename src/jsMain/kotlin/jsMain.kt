import calculator.StringCalculatorApp
import react.dom.*
import kotlinx.browser.document

fun main() {
    document.body!!.insertAdjacentHTML("afterbegin", "<div id='root'></div>" )
    render(document.getElementById("root")) {
        app {
            outputResults = mutableListOf()
            calcApp = StringCalculatorApp()
        }
    }
}