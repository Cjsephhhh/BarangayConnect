package com.barangayconnect.user;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="user_accounts",indexes={@Index(name="idx_user_email",columnList="email",unique=true),@Index(name="idx_user_mobile",columnList="mobile_number",unique=true)})
public class UserAccount {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=120) private String fullName;
 @Column(nullable=false,length=180,unique=true) private String email;
 @Column(name="mobile_number",nullable=false,length=20,unique=true) private String mobileNumber;
 @Column(nullable=false,length=220) private String address;
 @Column(nullable=false) private String passwordHash;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=32) private UserRole role=UserRole.RESIDENT;
 @Column(nullable=false) private boolean active=true;
 @Column(nullable=false) private int failedLoginAttempts=0;
 private Instant lockedUntil;
 @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
 private Instant updatedAt;
 @PreUpdate void touch(){updatedAt=Instant.now();}
 public Long getId(){return id;} public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
 public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getMobileNumber(){return mobileNumber;} public void setMobileNumber(String v){mobileNumber=v;}
 public String getAddress(){return address;} public void setAddress(String v){address=v;} public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;}
 public UserRole getRole(){return role;} public void setRole(UserRole v){role=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
 public int getFailedLoginAttempts(){return failedLoginAttempts;} public void setFailedLoginAttempts(int v){failedLoginAttempts=v;} public Instant getLockedUntil(){return lockedUntil;} public void setLockedUntil(Instant v){lockedUntil=v;} public Instant getCreatedAt(){return createdAt;}
}