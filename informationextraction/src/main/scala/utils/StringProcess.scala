package utils

object StringProcess {
  def camelCaseSplit(tokenToProcess: String): String = {
    tokenToProcess
      .replaceAll(
        String.format(
          "%s|%s|%s",
          "(?<=[A-Z])(?=[A-Z][a-z])",
          "(?<=[^A-Z])(?=[A-Z])",
          "(?<=[A-Za-z])(?=[^A-Za-z])"
        ),
        " "
      )
      .toLowerCase
      .trim
  }

  def removeNonAscii(token: String): String = {
    val processedLine: String = token.map(chr => {
      if (chr.isLetter)
        chr
      else ' '
    })

    processedLine
      .split(" ")
      .map(_.trim)
      .filter(_.nonEmpty)
      .mkString(" ")
  }
}
