package org.mule.weave.v2.module.http.client;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TLSSniSocketFactory extends SSLSocketFactory implements HandshakeCompletedListener {

  private SSLSocketFactory sslsf;

  public TLSSniSocketFactory(SSLSocketFactory _sslsf) {
    sslsf = _sslsf;
  }

  public void handshakeCompleted(HandshakeCompletedEvent var1) {

  }

  private Socket hookSocket(Socket sock) {
    SSLSocket ssl = (SSLSocket) sock;

    ssl.addHandshakeCompletedListener(this);
    return sock;
  }

  @Override
  public String[] getDefaultCipherSuites() {
    return sslsf.getDefaultCipherSuites();
  }

  @Override
  public String[] getSupportedCipherSuites() {
    return sslsf.getSupportedCipherSuites();
  }

  @Override
  public Socket createSocket(Socket socket, String s, int i, boolean b) throws IOException {
    return hookSocket(sslsf.createSocket(socket, s, i, b));
  }

  @Override
  public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
    return hookSocket(sslsf.createSocket(s, i));
  }

  @Override
  public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
    return hookSocket(sslsf.createSocket(s, i, inetAddress, i1));
  }

  @Override
  public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
    return hookSocket(sslsf.createSocket(inetAddress, i));
  }

  @Override
  public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
    return hookSocket(sslsf.createSocket(inetAddress, i, inetAddress1, i1));
  }
}