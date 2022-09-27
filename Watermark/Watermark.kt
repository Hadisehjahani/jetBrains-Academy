package watermark

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun main() {
    val imageSymbol = "image"
    println("Input the $imageSymbol filename:")
    val fileName = readln()
    val imageFile = File(fileName)
    isExist(imageFile)
    val image: BufferedImage = ImageIO.read(imageFile)
    isHave3ColorComponents(image, imageSymbol)
    isColorScheme24Or32(image, imageSymbol)

    val watermarkSymbol = "watermark"
    println("Input the $watermarkSymbol image filename:")
    val watermarkFileName = readln()
    val watermarkImageFile = File(watermarkFileName)
    isExist(watermarkImageFile)
    val watermark: BufferedImage = ImageIO.read(watermarkImageFile)
    isHave3ColorComponents(watermark, watermarkSymbol)
    isColorScheme24Or32(watermark, watermarkSymbol)

    if (image.width < watermark.width || image.height < watermark.height) {
        println("The watermark's dimensions are larger.")
        exitProcess(0)
    }

    var useAlpha = false
    var useTransparencyColor = false
    var transparentColor = Color(0)

    if (watermark.transparency == 3) {
        println("Do you want to use the watermark's Alpha channel?")
        val alphaAnswer = readln()
        if (alphaAnswer.lowercase() == "yes") {
            useAlpha = true
        }
    } else {
        println("Do you want to set a transparency color?")
        val transparencyColor = readln()
        if (transparencyColor.lowercase() == "yes") {
            useTransparencyColor = true
            println("Input a transparency color ([Red] [Green] [Blue]):")
            try {
                val tColor = readLine()!!.split(" ")
                val red = tColor[0]
                val green = tColor[1]
                val blue = tColor[2]

                if (red.toInt() !in 0..255 || green.toInt() !in 0..255 || blue.toInt() !in 0..255 || tColor.size != 3) {
                    println("The transparency color input is invalid.")
                    exitProcess(0)
                } else {
                    transparentColor = Color(tColor[0].toInt(), tColor[1].toInt(), tColor[2].toInt())
                }
            } catch (e: IndexOutOfBoundsException) {
                println("The transparency color input is invalid.")
                exitProcess(0)
            }
        } else {
            useTransparencyColor = false
        }
    }

    println("Input the watermark transparency percentage (Integer 0-100):")
    val wtc = readln()
    val weight: Int
    if (!isNumeric(wtc)) {
        println("The transparency percentage isn't an integer number.")
        exitProcess(0)
    } else {
        if (wtc.toInt() in 0..100) {
            weight = wtc.toInt()
        } else {
            println("The transparency percentage is out of range.")
            exitProcess(0)
        }
    }

    val diffX = image.width - watermark.width
    val diffY = image.height - watermark.height
    var watermarkX = 0
    var watermarkY = 0
    var singleMode = false
    var gridMode = false
    println("Choose the position method (single, grid):")
    val positionMethod = readln()
    when (positionMethod) {
        "single" -> {
            println("Input the watermark position ([x 0-$diffX] [y 0-$diffY]):")
            try {
                val position = readLine()!!.split(" ")
                watermarkX = position[0].toInt()
                watermarkY = position[1].toInt()

                if (watermarkX !in 0..diffX || watermarkY !in 0..diffY || position.size != 2) {
                    println("The position input is out of range.")
                    exitProcess(0)
                } else {
                    singleMode = true
                }
            } catch (e: NumberFormatException) {
                println("The position input is invalid.")
                exitProcess(0)
            }
        }
        "grid" -> {
            gridMode = true
        }
        else -> {
            println("The position method input is invalid.")
            exitProcess(0)
        }
    }

    println("Input the output image filename (jpg or png extension):")
    val outputFileName = readln()
    val extension = outputFileName.split(".")[1]
    if (!(extension == "jpg" || extension == "png")) {
        println("The output file extension isn't \"jpg\" or \"png\".")
        exitProcess(0)
    }

    val outputImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
    val outputImageFile = File(outputFileName)

    for (x in 0 until image.width) {
        for (y in 0 until image.height) {
            val i = Color(image.getRGB(x, y))
            var w = Color(0)

            // has alpha
            if (useAlpha) {
                if (singleMode) {
                    w = if ((x in watermarkX until watermarkX + watermark.width) && (y in watermarkY until watermarkY + watermark.height)) {
                        Color(watermark.getRGB(x - watermarkX, y - watermarkY), true)
                    } else {
                        Color(image.getRGB(x, y), true)
                    }
                } else if (gridMode) {
                    w = Color(watermark.getRGB(x % watermark.width, y % watermark.height), true)
                }
            }
            // hasn't alpha
            else {
                if (singleMode) {
                    w = if ((x in watermarkX until watermarkX + watermark.width) && (y in watermarkY until watermarkY + watermark.height)) {
                        Color(watermark.getRGB(x - watermarkX, y - watermarkY))
                    } else {
                        Color(image.getRGB(x, y))
                    }
                } else if (gridMode) {
                    w = Color(watermark.getRGB(x % watermark.width, y % watermark.height))
                }
            }

            if (w.alpha == 0 || (useTransparencyColor && w == transparentColor)) {
                outputImage.setRGB(x, y, i.rgb)
            } else {
                val color = Color(
                    (weight * w.red + (100 - weight) * i.red) / 100,
                    (weight * w.green + (100 - weight) * i.green) / 100,
                    (weight * w.blue + (100 - weight) * i.blue) / 100
                )
                outputImage.setRGB(x, y, color.rgb)
            }
        }
    }

    ImageIO.write(outputImage, extension, outputImageFile)
    println("The watermarked image $outputFileName has been created.")
}

fun isNumeric(toCheck: String): Boolean {
    val regex = "\\d+(\\d+)?".toRegex()
    return toCheck.matches(regex)
}

fun isExist(file: File) {
    if (!file.exists()) {
        println("The file $file doesn't exist.")
        exitProcess(0)
    }
}

fun isHave3ColorComponents(image: BufferedImage, name: String) {
    if (image.colorModel.numColorComponents != 3) {
        println("The number of $name color components isn't 3.")
        exitProcess(0)
    }
}

fun isColorScheme24Or32(image: BufferedImage, name: String) {
    if (!(image.colorModel.pixelSize == 24 || image.colorModel.pixelSize == 32)) {
        println("The $name isn't 24 or 32-bit.")
        exitProcess(0)
    }
}
