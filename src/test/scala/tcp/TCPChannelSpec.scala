package tcp

import cats.effect._
import cats.effect.unsafe.IORuntime
import fakes.FakeSocketChannel

class TCPChannelSpec extends munit.FunSuite with FakeSocketChannel {
  implicit val ioRuntime: IORuntime = IORuntime.global

  test("TCPChannel streams values from a socket") {
    val bytesRead = Array[Byte](1, 2, 3)
    val fakeChannel = new FakeReadableSocketChannel(bytesRead)
    val tcpChannel = TCPChannel.fromSocketChannel[IO](fakeChannel, bufferSize = 2)
    val result = tcpChannel.stream.take(bytesRead.length).compile.toList.unsafeRunSync()
    assertEquals(result, bytesRead.toList)
  }

  test("TCPChannel writes values to a socket") {
    val bytesToWrite = Array[Byte](1, 2, 3)
    val fakeChannel = new FakeWritableSocketChannel()
    val tcpChannel = TCPChannel.fromSocketChannel[IO](fakeChannel, bufferSize = 2)
    tcpChannel.write(bytesToWrite).unsafeRunSync()
    assertEquals(fakeChannel.bytes.toList, bytesToWrite.toList)
  }
}
