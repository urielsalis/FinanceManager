package me.urielsalis.financemanager.readers

import org.javamoney.moneta.Money
import java.math.BigDecimal

class LastMovementsReader: PDFReader<LastMovements>() {
    override fun preProcessData(text: String): PreProcessedData<LastMovements> {
        val split = text.split("\n")
        val movimientosList = mutableListOf<String>()

        if(split.isEmpty()) {
            return PreProcessedData(emptyMap(), emptyList())
        }

        var infoFinished = false
        var infoRow = 0
        var accountName = ""
        var accountCurrency = ""
        var accountAmount = ""
        val date = split[0]

        for (temp in split) {
            val line = temp.trim()
            if(!line.matches("^\\d*\\/\\d*.*\$".toRegex())) { //if its just a data/page number, ignore
                if(!infoFinished) {
                    if(line == "Fecha Descripción Monto ARS") {
                        infoFinished = true
                    } else {
                        infoRow++;
                        when(infoRow) {
                            1 -> {
                                val saldoTemp = line.split("Saldo")
                                accountName = saldoTemp[0].trim()
                                accountAmount = saldoTemp[1].split(" ")[2]
                            }
                            2 -> accountName += " " + line
                            3 -> accountCurrency = line.split(" ")[1]
                        }
                    }
                } else {
                    if(line != "Fecha Descripción Monto ARS") {
                        if(line.matches("^\\d*\\s\\w*.*".toRegex())) {
                            val lineSplit = line.split(" ")
                            val movementDate = "${lineSplit[2]} - ${lineSplit[1]} - ${lineSplit[0]}"
                            val movementAmount = lineSplit.last()
                            val rest = line.substringBefore(movementAmount)
                                    .trim()
                                    .substringAfter("${lineSplit[0]} ${lineSplit[1]} ${lineSplit[2]}")
                                    .trim()
                            var movementOperation = ""
                            var movementName = ""
                            if(rest.startsWith("COMPRA - ESTABLECIMIENTO: ")) {
                                movementOperation = "COMPRA"
                                movementName = rest.substringAfter("COMPRA - ESTABLECIMIENTO:").trim()
                            } else if(rest.startsWith("TRAN.ELECTRONIC")) {
                                movementOperation = "TRANSFERENCIA"
                                movementName = rest.substringAfter("TRAN.ELECTRONIC:").trim()
                            } else if(rest.startsWith("DEP. CAJ.AUTOM")) {
                                movementOperation = "DEPOSITO"
                                movementName = rest.substringAfter("DEP. CAJ.AUTOM.").trim()
                            } else if(rest.startsWith("ACRED.INTERESES")) {
                                movementOperation = "INTERESES"
                                movementName = rest.substringAfter("ACRED.INTERESES.").trim()
                            } else if(rest.startsWith("ACRED.INTERESES")) {
                                movementOperation = "EXTRACCION"
                                movementName = rest.substringAfter("EXT. CAJ.AUTOM.").trim()
                            } else {
                                movementOperation = "TRANSFERENCIA"
                                movementName = rest
                            }
                            movimientosList.add("$movementDate; $movementAmount; $movementOperation; $movementName")
                        } else {
                            val last = movimientosList.last()
                            movimientosList.removeAt(movimientosList.lastIndex)
                            movimientosList.add("$last $line".trim())
                        }
                    }
                }
            }
        }
        val header = mapOf<String, String>(
                Pair("Name", accountName),
                Pair("Currency", accountCurrency),
                Pair("Amount", accountAmount),
                Pair("Date", date))

        return PreProcessedData(header, movimientosList)
    }

    override fun writeData(data: PreProcessedData<LastMovements>): LastMovements {
        val movimientos = mutableListOf<Movement>()
        for(line in data.data) {
            val split = line.split(";")
            var amount = split[1].trim()
                    .replace(".", "")
                    .replace(",", ".");
            movimientos.add(Movement(split[3],
                    Money.of(
                            BigDecimal(amount),
                            data.header["Currency"]),
                    split[2].trim(),
                    split[0].trim()))
        }

        return LastMovements(data.header["Name"]!!,
                Money.of(BigDecimal(
                        data.header["Amount"]!!
                                .replace(".", "")
                                .replace(",", ".")),
                        data.header["Currency"]), movimientos)
    }

}

data class LastMovements(val accountName: String, val accountFinalBalance: Money, val movements: List<Movement>)
data class Movement(val name: String, val amount: Money, val operation: String, val date: String)
