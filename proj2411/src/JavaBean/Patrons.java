package JavaBean;

public class Patrons implements BeanInterface{
/*	private String HKid;
	private String Email;
	private String Username;
	private String Password;
	private boolean isActivated;

	Patrons(){};*/
	private String hkid;
	private String email;
	private String name;
	private String password;
	private boolean activestat;

	public Patrons(){};

	public Patrons(String HKid, String username, String password, String email,  String activestat) {
		setHkid(HKid);
		setEmail(email);
		setName(username);
		setPassword(password);
		setActivestat(activestat);
	}

	public void setActivestat(String activestat) {
		this.activestat = activestat.equals("T");
		
	}

	public String getHkid() {
		return hkid;
	}

	public void setHkid(String hkid) {
		this.hkid = hkid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String username) {
		name = username;
	}


	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActivated() {
		return activestat;
	}
	@Override
	public String toString(){
		return String.format("%s %s %s %s",getHkid(),getName(),getEmail(),activestat?"Activated":"Deactivated");
	}

	@Override
	public String getID() {
		return getHkid();
	}
}
