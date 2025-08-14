package me.huangjiacheng.hundun.model;

/**
 * 港股基本信息实体，对应AKShare接口stock_hk_company_profile_em返回
 */
public class StockHkBasicInfo {
    /** 公司统一社会信用代码/注册号 */
    private String comunic;
    /** 公司中文名 */
    private String comcnname;
    /** 公司英文名 */
    private String comenname;
    /** 成立日期（时间戳） */
    private Long incdate;
    /** 注册地址 */
    private String rgiofc;
    /** 总部/办公地址 */
    private String hofclctmbu;
    /** 董事长 */
    private String chairman;
    /** 主营业务 */
    private String mbu;
    /** 公司简介 */
    private String comintr;
    /** 所属行业 */
    private String industry;
    /** 法定代表人 */
    private String legalRepresentative;
    /** 注册资本 */
    private String registeredCapital;
    /** 经营范围 */
    private String businessScope;
    /** 公司网址 */
    private String website;
    /** 公司邮箱 */
    private String email;
    /** 联系电话 */
    private String phone;

    // Getter和Setter方法
    public String getComunic() { return comunic; }
    public void setComunic(String comunic) { this.comunic = comunic; }
    
    public String getComcnname() { return comcnname; }
    public void setComcnname(String comcnname) { this.comcnname = comcnname; }
    
    public String getComenname() { return comenname; }
    public void setComenname(String comenname) { this.comenname = comenname; }
    
    public Long getIncdate() { return incdate; }
    public void setIncdate(Long incdate) { this.incdate = incdate; }
    
    public String getRgiofc() { return rgiofc; }
    public void setRgiofc(String rgiofc) { this.rgiofc = rgiofc; }
    
    public String getHofclctmbu() { return hofclctmbu; }
    public void setHofclctmbu(String hofclctmbu) { this.hofclctmbu = hofclctmbu; }
    
    public String getChairman() { return chairman; }
    public void setChairman(String chairman) { this.chairman = chairman; }
    
    public String getMbu() { return mbu; }
    public void setMbu(String mbu) { this.mbu = mbu; }
    
    public String getComintr() { return comintr; }
    public void setComintr(String comintr) { this.comintr = comintr; }
    
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    
    public String getLegalRepresentative() { return legalRepresentative; }
    public void setLegalRepresentative(String legalRepresentative) { this.legalRepresentative = legalRepresentative; }
    
    public String getRegisteredCapital() { return registeredCapital; }
    public void setRegisteredCapital(String registeredCapital) { this.registeredCapital = registeredCapital; }
    
    public String getBusinessScope() { return businessScope; }
    public void setBusinessScope(String businessScope) { this.businessScope = businessScope; }
    
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
} 