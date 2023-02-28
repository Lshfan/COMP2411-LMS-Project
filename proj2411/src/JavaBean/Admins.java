package JavaBean;

public class Admins {
	String name;
	String password;
	String hkid;
	
	public Admins(String name,String password,String hkid){
		this.setHKid(hkid);
		this.setName(name);
		this.password = password;
	}
	
	public Admins() {
		
	}
	
	public String getID() {
		return hkid;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String newpwd) {
		this.password = newpwd;
	}
	
	@Override
	public String toString() {
		return name+"\t"+password;
	}

	public void setHKid(String HKid) {
		this.hkid = HKid;
	}

	public void setName(String username) {
		this.name = username;
	}
}
