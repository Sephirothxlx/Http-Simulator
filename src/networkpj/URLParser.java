package networkpj;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLParser {
	private Pattern pattern;
	private Matcher matcher;
	private String url;
	private String protocol;
	private String authority;
	private int port = -1;
	private String path;
	private String query;
	private String fragment;

	public URLParser(String s) {
		this.url = s;
		this.pattern = Pattern.compile("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");
		this.matcher = this.pattern.matcher(this.url);
		if (this.matcher.find()) {
			this.protocol = this.matcher.group(2);
			this.authority = this.matcher.group(4);
			if (this.authority.contains(":")) {
				String temp = this.authority.substring(this.authority.indexOf(':') + 1, this.authority.length());
				this.authority = this.authority.substring(0, this.authority.indexOf(':'));
				this.port = Integer.parseInt(temp);
			}
			this.path = this.matcher.group(5);
			if(this.path.equals(""))
				this.path="/";
			this.query = this.matcher.group(7);
			this.fragment = this.matcher.group(9);
		}
	}

	public String getProtocol() {
		return this.protocol;
	}

	public String getAuthority() {
		return this.authority;
	}

	public int getPort() {
		return this.port;
	}

	public String getPath() {
		return this.path;
	}

	public String getQuery() {
		return this.query;
	}

	public String getFragment() {
		return this.fragment;
	}
}
