package networkpj;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DNS {

	public DNS() {

	}

	public String queryIPv4(String host) throws Exception {
		String ipaddr = "";
		String[] s = host.split("\\.");
		InetAddress dnsaddr = InetAddress.getByName("202.120.224.26");
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bs);
		out.writeShort(0x1234);
		out.writeShort(0x0100);
		out.writeShort(0x0001);
		out.writeShort(0x0000);
		out.writeShort(0x0000);
		out.writeShort(0x0000);
		for (int i = 0; i < s.length; i++) {
			byte[] b = s[i].getBytes("UTF-8");
			out.writeByte(b.length);
			out.write(b);
		}
		out.writeByte(0x00);
		out.writeShort(0x0001);
		out.writeShort(0x0001);
		byte[] query = bs.toByteArray();
		DatagramSocket socket = new DatagramSocket();
		DatagramPacket sendpacket = new DatagramPacket(query, query.length, dnsaddr, 53);
		socket.send(sendpacket);
		byte[] buf = new byte[1024];
		DatagramPacket receivepacket = new DatagramPacket(buf, buf.length);
		socket.receive(receivepacket);
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(buf));
		in.skipBytes(12);
		int l = 0;
		while ((l = in.readByte()) > 0) {
			in.skipBytes(l);
		}
		in.skipBytes(4);
		a: while (true) {
			while ((l = in.readByte() & 0xFF) > 0) {
				if (l == 192) {
					in.skipBytes(1);
					break;
				} else
					in.skipBytes(l);
			}
			in.skipBytes(8);
			l = in.readShort();
			if (l == 4) {
				for (int i = 0; i < 4; i++) {
					ipaddr += String.format("%d", (in.readByte() & 0xFF)) + ".";
				}
				ipaddr = ipaddr.substring(0, ipaddr.length() - 1);
				break;
			} else {
				in.skipBytes(l);
				if (in.available() == 0)
					break a;
			}
		}
		System.err.write(ipaddr.getBytes());

		return ipaddr;
	}

	public byte[] queryIPv6(String host) throws Exception {
		String addr = "";
		byte[] ipaddr = new byte[16];
		String[] s = host.split("\\.");
		InetAddress dnsaddr = InetAddress.getByName("202.120.224.26");
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bs);
		out.writeShort(0x1234);
		out.writeShort(0x0100);
		out.writeShort(0x0001);
		out.writeShort(0x0000);
		out.writeShort(0x0000);
		out.writeShort(0x0000);
		for (int i = 0; i < s.length; i++) {
			byte[] b = s[i].getBytes("UTF-8");
			out.writeByte(b.length);
			out.write(b);
		}
		out.writeByte(0x00);
		out.writeShort(0x001c);
		out.writeShort(0x0001);
		byte[] query = bs.toByteArray();
		DatagramSocket socket = new DatagramSocket();
		DatagramPacket sendpacket = new DatagramPacket(query, query.length, dnsaddr, 53);
		socket.send(sendpacket);
		byte[] buf = new byte[1024];
		DatagramPacket receivepacket = new DatagramPacket(buf, buf.length);
		socket.receive(receivepacket);
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(buf));

		in.skipBytes(12);
		int l = 0;
		while ((l = in.readByte()) > 0) {
			in.skipBytes(l);
		}
		in.skipBytes(4);
		a: while (true) {
			while ((l = in.readByte() & 0xFF) > 0) {
				if (l == 192) {
					in.skipBytes(1);
					break;
				} else
					in.skipBytes(l);
			}
			in.skipBytes(8);
			l = in.readShort();
			if (l == 16) {
				for (int i = 0; i < 16; i++) {
					ipaddr[i] = in.readByte();
				}
				InetAddress ia = InetAddress.getByAddress(ipaddr);
				addr = ia.getHostAddress();
				addr = addr.replaceAll(":0:0:", "::");
				break;
			} else {
				in.skipBytes(l);
				if (in.available() == 0)
					break a;
			}
		}
		System.err.write(addr.getBytes());
		return ipaddr;
	}

}
