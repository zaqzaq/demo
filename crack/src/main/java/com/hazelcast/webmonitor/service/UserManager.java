package com.hazelcast.webmonitor.service;

import com.hazelcast.instance.BuildInfo;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.license.domain.Feature;
import com.hazelcast.license.domain.License;
import com.hazelcast.license.domain.LicenseType;
import com.hazelcast.license.exception.InvalidLicenseException;
import com.hazelcast.license.util.LicenseHelper;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.webmonitor.model.User;
import com.hazelcast.webmonitor.repositories.UserRepository;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserManager
        implements AuthenticationProvider
{
    private static final ILogger logger = Logger.getLogger(UserManager.class);
    private UserRepository userRepository = new UserRepository();
    private License license;
    private boolean licenseConfigured;
    private String licenseInfo;

   /* public UserManager()
    {
        String key = this.userRepository.getLicenseKey();
        if (key != null) {
            try
            {
                this.license = LicenseHelper.checkLicenseKeyPerFeature(key, BuildInfoProvider.getBuildInfo().getVersion(), Feature.MAN_CENTER);

                this.licenseInfo = generateLicenseInfo();
                this.licenseConfigured = true;
            }
            catch (InvalidLicenseException ex)
            {
                logger.warning(ex.getMessage());
            }
        }
    }*/

    public UserManager()
    {
        this.license = new License();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            license.setExpiryDate(df.parse("2222-12-12"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        license.setAllowedNativeMemorySize(Integer.MAX_VALUE);
        license.setAllowedNumberOfNodes(100);
        license.setCompanyName("freshz");
        license.setCreationDate(new Date());
        license.setCreatorEmail("zyj@freshz.cn");
//        license.setCustomTypeId();

        ArrayList<Feature> features = new ArrayList();
        features.add(Feature.HOT_RESTART);
        license.setFeatures(features);

        this.licenseInfo = generateLicenseInfo();
        this.licenseConfigured = true;
    }

    public License getLicense()
    {
        if (this.license == null) {
            throw new InvalidLicenseException("License is not configured!!!");
        }
        return this.license;
    }

    public String getDirectory()
    {
        return this.userRepository.getHomeDirectory();
    }

    public WriteMode getWriteMode()
    {
        return WriteMode.get(this.userRepository.getWriteMode());
    }

    public void setWriteMode(WriteMode newWriteMode)
    {
        this.userRepository.saveWriteMode(newWriteMode.value());
    }

    public String getLicenseInfo()
    {
        if ((isLicenseConfigured()) && (this.license.getExpiryDate().getTime() > new Date().getTime())) {
            return this.licenseInfo;
        }
        return "EXPIRED";
    }

    private String generateLicenseInfo()
    {
//        DateFormat df = new SimpleDateFormat("MMMM,dd yyyy");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

//        return "<br/><b>License End Date: </b>六月,15 2117";

        return "<br/><b>License End Date: </b>" + df.format(this.license.getExpiryDate());
    }

    public static void main(String[] args) {
        DateFormat df = new SimpleDateFormat("MMMM,dd yyyy");
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");

        System.out.println(df2.format(new Date()));
        System.out.println(df.format(new Date()));
    }

    public long persistLicenseKey(String key)
    {
        long licenseExpiryTime = 0L;
        try
        {
            this.license = LicenseHelper.checkLicenseKeyPerFeature(key, BuildInfoProvider.getBuildInfo().getVersion(), Feature.MAN_CENTER);

            this.userRepository.saveLicenseKey(key);
            this.licenseConfigured = true;
            licenseExpiryTime = this.license.getExpiryDate().getTime();
            this.licenseInfo = generateLicenseInfo();
        }
        catch (InvalidLicenseException e)
        {
            logger.warning("License Problem occured:", e);
            this.licenseConfigured = false;
            this.license = null;
        }
        return licenseExpiryTime;
    }

    public User login(String username, String password)
    {
        if (this.userRepository.getUserKeys().size() == 0)
        {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setAdmin(1);
            user.setRoles("");
            user.setEmail("");
            try
            {
                this.userRepository.save(user);
            }
            catch (IOException e)
            {
                logger.warning(e);
            }
        }
        return this.userRepository.login(username, password);
    }

    public List<String> getUserNames()
    {
        return this.userRepository.getUserKeys();
    }

    public User loadUser(String username)
    {
        return this.userRepository.get(username);
    }

    public void saveUser(String user, String email, String pass, int admin, String roles)
    {
        try
        {
            this.userRepository.save(new User(user, email, pass, admin, roles));
        }
        catch (IOException e)
        {
            logger.warning(e);
        }
    }

    public void deleteUser(String user)
    {
        try
        {
            this.userRepository.delete(user);
        }
        catch (Exception e)
        {
            logger.warning(e);
        }
    }

    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException
    {
        String username = (String)authentication.getPrincipal();
        String password = (String)authentication.getCredentials();
        User user = login(username, password);
        if (user != null)
        {
            List<GrantedAuthority> grantedAuths = new ArrayList();
            if (user.getAdmin() == 1) {
                grantedAuths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            } else {
                grantedAuths.add(new SimpleGrantedAuthority("ROLE_READONLY_USER"));
            }
            if (!"".equals(user.getRoles())) {
                grantedAuths.add(new SimpleGrantedAuthority(user.getRoles()));
            }
            return new UsernamePasswordAuthenticationToken(user, null, grantedAuths);
        }
        return null;
    }

    public boolean supports(Class<?> authentication)
    {
        return true;
    }

    public boolean hasValidLicense()
    {
        boolean featureCheck;
        try
        {
            if (this.license == null) {
                throw new InvalidLicenseException("License is not configured.");
            }
            //不检测啦
            // LicenseHelper.checkLicenseKeyPerFeature(this.license.getKey(), BuildInfoProvider.getBuildInfo().getVersion(), Feature.MAN_CENTER);

            featureCheck = true;
        }
        catch (InvalidLicenseException e)
        {
            featureCheck = false;
        }
        if (isLicenseConfigured()) {
            return (featureCheck) && (!LicenseHelper.isExpired(this.license));
        }
        return false;
    }

    public boolean isLicenseConfigured()
    {
        return this.licenseConfigured;
    }

    public LicenseType getLicenseType()
    {
        if (this.license.getFeatures().contains(Feature.HOT_RESTART)) {
            return LicenseType.ENTERPRISE_HD;
        }
        return LicenseType.ENTERPRISE;
    }
}
