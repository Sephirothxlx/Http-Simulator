package networkpj;

public class Entrance {
	public static void main(String[] args) {
		URLParser p = new URLParser(args[0]);
//		URLParser p = new URLParser("https://mirrors.tuna.tsinghua.edu.cn/centos/RPM-GPG-KEY-CentOS-7");
		DNS dq = new DNS();
		try {
			byte[] ip = dq.queryIPv6(p.getAuthority());
			if (ip.length != 0) {
				if (p.getProtocol().equals("http")) {
					Http h = new Http();
					h.getIPv6(p, ip);
				} else if (p.getProtocol().equals("https")) {
					Https h = new Https();
					h.getIPv6(p, ip);
				}
			}
		} catch (Exception e) {
			try {
				String ip = dq.queryIPv4(p.getAuthority());
				if (!ip.equals("")) {
					if (p.getProtocol().equals("http")) {
						Http h = new Http();
						h.getIPv4(p, ip);
					} else if (p.getProtocol().equals("https")) {
						Https h = new Https();
						h.getIPv4(p, ip);
					}
				}
			} catch (Exception ex) {
				System.exit(0);
			}
		}
	}
}
