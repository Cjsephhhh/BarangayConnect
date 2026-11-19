package com.barangayconnect.auth;
import com.barangayconnect.user.UserAccount;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="login_sessions",indexes=@Index(name="idx_session_jti",columnList="jti",unique=true))
public class LoginSession {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) private UserAccount user;
 @Column(nullable=false,unique=true,length=80) private String jti;
 @Column(nullable=false) private Instant expiresAt;
 @Column(nullable=false) private Instant lastActivityAt;
 @Column(nullable=false) private boolean revoked=false;
 protected LoginSession(){}
 public LoginSession(UserAccount user,String jti,Instant expiresAt){this.user=user;this.jti=jti;this.expiresAt=expiresAt;this.lastActivityAt=Instant.now();}
 public Long getId(){return id;} public UserAccount getUser(){return user;} public String getJti(){return jti;} public Instant getExpiresAt(){return expiresAt;} public Instant getLastActivityAt(){return lastActivityAt;} public void setLastActivityAt(Instant v){lastActivityAt=v;} public boolean isRevoked(){return revoked;} public void setRevoked(boolean v){revoked=v;}
}