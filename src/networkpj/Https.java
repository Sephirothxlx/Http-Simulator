package networkpj;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Https {

	static boolean isValid = true;
	static boolean isChunked = false;
	static boolean isGzip = false;

	public void getIPv4(URLParser p, String ip) throws Exception {
		SSLContext sc = SSLContext.getInstance("TLSv1");
		sc.init(null, null, null);
		SSLSocketFactory factory = (SSLSocketFactory) sc.getSocketFactory();
		SSLSocket socket = (SSLSocket) factory.createSocket();
		SocketAddress sa = null;
		InetAddress ia = InetAddress.getByName(ip);
		String port = "";
		try {
			if (p.getPort() == -1) {
				sa = new InetSocketAddress(ia, 443);
			} else {
				port = ":" + p.getPort();
				sa = new InetSocketAddress(ia, p.getPort());
			}
			socket.connect(sa, 5000);
		} catch (SocketTimeoutException e) {
			return;
		}

		OutputStream out = socket.getOutputStream();
		StringBuffer s = new StringBuffer();
		s.append("GET ");
		s.append(p.getPath() + " HTTP/1.1\r\n");
		s.append("Host: " + p.getAuthority() + port + "\r\n");
		s.append("Connection: close\r\n");
		s.append("Cache-Control: max-age=0\r\n");
		s.append("Upgrade-Insecure-Requests: 1\r\n");
		s.append(
				"User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X10_10_4) AppleWebKit/537.36 (KHTML, like Gecko)Chrome/55.0.2883.95 Safari/537.36\r\n");
		s.append("Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n");
		s.append("Accept-Encoding: gzip, deflate, sdch\r\n");
		s.append("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4\r\n");
		s.append("\r\n");
		System.err.write(s.toString().getBytes());
		out.write(s.toString().getBytes());
		out.flush();

		InputStream in = socket.getInputStream();
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		DataOutputStream o = new DataOutputStream(bs);
		String header = "";
		int c = 0;
		int x = 0;
		while ((c = in.read()) != -1) {
			o.writeByte(c);
			if ((char) c == '\r' || (char) c == '\n')
				x++;
			else
				x = 0;
			if (x == 4)
				break;
		}
		header = bs.toString();
		System.err.write(header.getBytes());

		checkHeader(header);

		byte[] body = null;
		if (isValid == true) {
			if (isChunked == true) {
				byte[] b = getChunkedBody(in);
				if (isGzip == true)
					body = gzipSolve(b);
				else
					body = chunkSolve(b);
				System.out.write(body);
			} else {
				body = getBody(in);
				System.out.write(body);
			}
		}
		out.close();
		in.close();
		socket.close();
	}

	public void getIPv6(URLParser p, byte[] ip) throws Exception {
		SSLContext sc = SSLContext.getInstance("TLSv1");
		sc.init(null, null, new java.security.SecureRandom());
		SSLSocketFactory factory = (SSLSocketFactory) sc.getSocketFactory();
		SSLSocket socket = (SSLSocket) factory.createSocket();
		SocketAddress sa = null;
		InetAddress ia = InetAddress.getByAddress(p.getAuthority(), ip);
		String port = "";
		try {
			if (p.getPort() == -1) {
				sa = new InetSocketAddress(ia, 443);
			} else {
				port = ":" + p.getPort();
				sa = new InetSocketAddress(ia, p.getPort());
			}
			socket.connect(sa, 5000);
		} catch (SocketTimeoutException e) {
			return;
		}
		OutputStream out = socket.getOutputStream();// 字节输出流
		StringBuffer s = new StringBuffer();
		s.append("GET ");
		s.append(p.getPath() + " HTTP/1.1\r\n");
		s.append("Host: " + p.getAuthority() + port + "\r\n");
		s.append("Connection: close\r\n");
		s.append("Cache-Control: max-age=0\r\n");
		s.append("Upgrade-Insecure-Requests: 1\r\n");
		s.append(
				"User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X10_10_4) AppleWebKit/537.36 (KHTML, like Gecko)Chrome/55.0.2883.95 Safari/537.36\r\n");
		s.append("Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n");
		s.append("Accept-Encoding: gzip, deflate, sdch\r\n");
		s.append("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4\r\n");
		s.append("\r\n");
		System.err.write(s.toString().getBytes());
		out.write(s.toString().getBytes());
		out.flush();

		InputStream in = socket.getInputStream();
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		DataOutputStream o = new DataOutputStream(bs);
		String header = "";
		int c = 0;
		int x = 0;
		while ((c = in.read()) != -1) {
			o.writeByte(c);
			if ((char) c == '\r' || (char) c == '\n')
				x++;
			else
				x = 0;
			if (x == 4)
				break;
		}
		header = bs.toString();
		System.err.write(header.getBytes());

		checkHeader(header);

		byte[] body = null;
		if (isValid == true) {
			if (isChunked == true) {
				byte[] b = getChunkedBody(in);
				if (isGzip == true)
					body = gzipSolve(b);
				else
					body = chunkSolve(b);
				System.out.write(body);
			} else {
				body = getBody(in);
				System.out.write(body);
			}
		}
		out.close();
		in.close();
		socket.close();

	}

	public void checkHeader(String header) {
		// 检查是否response破损
		String[] headerline = header.split("\r\n");
		for (int i = 0; i < headerline.length; i++) {
			String[] temp = headerline[i].split(": ");
			if (i == 0 && !temp[0].equals("HTTP/1.1 200 OK")) {
				isValid = false;
				break;
			} else if (temp[0].equals("Connection")) {
				if (temp[1].toLowerCase().equals("keep-alive") || temp[1].toLowerCase().equals("close"))
					continue;
				else {
					isValid = false;
					break;
				}
			} else if (temp[0].equals("Content-Length")) {
				try {
					int length = Integer.parseInt(temp[1]);
					if (length < 0) {
						isValid = false;
						break;
					}
				} catch (Exception e) {
					isValid = false;
					break;
				}
			} else if (temp[0].equals("Transfer-Encoding")) {
				if (temp[1].equals("chunked")) {
					isChunked = true;
				}
			} else if (temp[0].equals("Content-Encoding")) {
				if (temp[1].equals("gzip")) {
					isGzip = true;
				}
			}
		}
	}

	public byte[] getBody(InputStream in) {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		DataOutputStream o = new DataOutputStream(bs);
		int c = 0;
		try {
			while ((c = in.read()) != -1) {
				o.writeByte(c);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bs.toByteArray();
	}

	public byte[] getChunkedBody(InputStream in) {
		int i = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			while (true) {
				String temp = getChunkSize(in);
				if (temp.equals("0"))
					break;
				i = Integer.valueOf(temp, 16);
				int count = 0;
				while (count < i) {
					out.write(in.read());
					count++;
				}
				in.skip(2);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "".getBytes();
		}
		return out.toByteArray();
	}

	public String getChunkSize(InputStream in) throws IOException {
		int c = 0;
		String s = "";
		while ((c = in.read()) != -1) {
			s = s + (char) c + "";
			if (c == '\r') {
				if ((char) in.read() == '\n')
					break;
			}
		}
		s = s.trim();
		return s;
	}

	public byte[] chunkSolve(byte[] body) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (int i = 0; i < body.length; i++) {
			out.write(body[i]);
		}
		return out.toByteArray();
	}

	public byte[] gzipSolve(byte[] body) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(body);
		GZIPInputStream gz;
		try {
			gz = new GZIPInputStream(in);
			int count = 0;
			int n = 0;
			while ((n = gz.read()) >= 0) {
				out.write(n);
				count++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out.toByteArray();
	}
}
