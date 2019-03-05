package chat.tox.antox.tox

import java.io._

import android.content.Context
import chat.tox.antox.utils.{AntoxLog, FileUtils}
import im.tox.tox4j.core.options.SaveDataOptions
import im.tox.tox4j.core.options.SaveDataOptions.ToxSave
import im.tox.tox4j.impl.jni.ToxCryptoImpl
import org.scaloid.common.LoggerTag

object ToxDataFile {

  def isEncrypted(data: Array[Byte]): Boolean = {
    ToxCryptoImpl.isDataEncrypted(data)
  }

  def isEncrypted(file: File): Boolean = {
    var fin: FileInputStream = null
    var data: Array[Byte] = null
    try {
      fin = new FileInputStream(file)
      data = Array.ofDim[Byte](file.length.toInt)
      fin.read(data)
    } catch {
      case e: FileNotFoundException => e.printStackTrace()
      case e: IOException => e.printStackTrace()
    } finally {
      try {
        if (fin != null) {
          fin.close()
        }
      } catch {
        case ioe: IOException => ioe.printStackTrace()
      }
    }
    ToxDataFile.isEncrypted(data)
  }


}

class ToxDataFile(ctx: Context, fileName: String) {

  private val TAG = LoggerTag(getClass.getName)

  def getFile(): File = {
    return new File(ctx.getExternalFilesDir(null).getPath() + File.separator + fileName)
  }

  def isEncrypted: Boolean = {
    ToxDataFile.isEncrypted(getFile())
  }

  def decrypt(pass: String): Unit = {
    if (isEncrypted) {
      val data = loadFile()
      val salt = ToxCryptoImpl.getSalt(data)
      saveFile(ToxCryptoImpl.decrypt(data, ToxCryptoImpl.passKeyDeriveWithSalt(pass.getBytes, salt)))
    }
  }

  def doesFileExist(): Boolean = {
    AntoxLog.debug("fileName: " + fileName, TAG)
    val selfFile = getFile()
    if (selfFile == null) {
      AntoxLog.debug("selfFile is null!", TAG)
    }
    selfFile.exists()
  }

  def exportFile(dest: File): Unit = {
    if (!dest.exists()) {
      throw new IllegalArgumentException("dest must exist")
    }

    FileUtils.copy(getFile(), new File(dest + "/" + fileName + ".tox"))
  }

  def deleteFile() {
    getFile().delete()
  }

  def loadFile(): Array[Byte] = {
    var fin: FileInputStream = null
    val file = getFile()
    var data: Array[Byte] = null
    try {
      fin = new FileInputStream(file)
      data = Array.ofDim[Byte](file.length.toInt)
      fin.read(data)
    } catch {
      case e: FileNotFoundException => e.printStackTrace()
      case e: IOException => e.printStackTrace()
    } finally {
      try {
        if (fin != null) {
          fin.close()

        }
      } catch {
        case ioe: IOException => ioe.printStackTrace()
      }
    }
    data
  }

  def loadEncryptedFile(pass: String): Array[Byte] = {
    var fin: FileInputStream = null
    val file = getFile()
    var data: Array[Byte] = null
    try {
      fin = new FileInputStream(file)
      data = Array.ofDim[Byte](file.length.toInt)
      fin.read(data)
    } catch {
      case e: FileNotFoundException => e.printStackTrace()
      case e: IOException => e.printStackTrace()
    } finally {
      try {
        if (fin != null) {
          fin.close()
        }
      } catch {
        case ioe: IOException => ioe.printStackTrace()
      }
    }
    val salt = ToxCryptoImpl.getSalt(data)
    ToxCryptoImpl.decrypt(data, ToxCryptoImpl.passKeyDeriveWithSalt(pass.getBytes, salt))
  }

  def loadAsSaveType(): SaveDataOptions = {
    if (doesFileExist()) {
      ToxSave(loadFile())
    } else {
      SaveDataOptions.None
    }
  }

  def saveFile(dataToBeSaved: Array[Byte]) {
    val myFile = getFile()
    try {
      myFile.createNewFile()
    } catch {
      case e1: IOException => e1.printStackTrace()
    }
    try {
      val output = new FileOutputStream(myFile)
      output.write(dataToBeSaved, 0, dataToBeSaved.length)
      output.close()
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }
}
