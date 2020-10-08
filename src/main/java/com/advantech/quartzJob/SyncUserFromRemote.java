/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.quartzJob;

import com.advantech.dao.db1.FloorDAO;
import com.advantech.dao.db1.UnitDAO;
import com.advantech.dao.db1.UserDAO;
import com.advantech.dao.db1.UserProfileDAO;
import com.advantech.dao.db3.SqlViewDAO;
import com.advantech.helper.CustomPasswordEncoder;
import com.advantech.model.db1.Floor;
import com.advantech.model.db1.Unit;
import com.advantech.model.db1.User;
import com.advantech.model.db1.UserInfoOnMes;
import com.advantech.model.db1.UserProfile;
import com.advantech.security.State;
import com.advantech.webservice.Factory;
import com.advantech.webservice.WebServiceRV;
import static com.google.common.collect.Lists.newArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Component
@Transactional
public class SyncUserFromRemote {

    private static final Logger logger = LoggerFactory.getLogger(SyncUserFromRemote.class);

    @Autowired
    @Qualifier("sqlViewDAO3")
    private SqlViewDAO sqlViewDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private CustomPasswordEncoder pswEncoder;

    @Autowired
    private FloorDAO floorDAO;

    @Autowired
    private UnitDAO unitDAO;

    @Autowired
    private UserProfileDAO userProfileDAO;

    private List<UserProfile> userProfiles;

    @Autowired
    private WebServiceRV rv;

    public void execute() throws Exception {
        List<UserInfoOnMes> l = rv.getUsersInfoOnMes(Factory.DEFAULT);
        List<UserInfoOnMes> remoteDirectUser = l.stream()
                .filter(ur -> (ur.getUnitNo() != null && ur.getUnitNo().matches("(A|B|T|P)")))
                .collect(toList());

        List<User> users = userDAO.findByRole("PREASSY_USER", "ASSY_USER", "TEST_USER", "PACKING_USER");

        Floor f = floorDAO.findByPrimaryKey(4);

        Unit mfg = unitDAO.findByPrimaryKey(1);

        userProfiles = userProfileDAO.findAll();

        //Compare which jobnumber is new and which number is old
        //沒在User_Profile_REF的User會被insert進去
        if (!remoteDirectUser.isEmpty() && !users.isEmpty()) {

            remoteDirectUser.forEach(ru -> {
                User matchesUser = users.stream()
                        .filter(a -> a.getJobnumber().equals(ru.getUserNo()))
                        .findFirst()
                        .orElse(null);

                if (matchesUser == null) {
                    //insert new one
                    User userWithoutRole = userDAO.findByJobnumber(ru.getUserNo());

                    User user = new User();
                    user.setJobnumber(ru.getUserNo());
                    user.setUsername(ru.getUserNameCh());
                    user.setUsernameCh(ru.getUserNameCh());
                    user.setPassword(pswEncoder.encode(ru.getUserNo()));
                    user.setState(State.ACTIVE);

                    user.setFloor(f);

                    user.setUnit(mfg);

                    setUserProfle(user, ru.getUnitNo());

                    userDAO.insert(user);
                    System.out.println("insert" + " " + ru.getUserNo());

                } else {
                    //Find user's department & floor is matches or not
                    //if not match, update department & floor
                    setUserProfle(matchesUser, ru.getUnitNo());

                    matchesUser.setUsername(ru.getUserNameCh());
                    matchesUser.setUsernameCh(ru.getUserNameCh());

                    matchesUser.setState(State.ACTIVE);
                    userDAO.update(matchesUser);
                    System.out.println("update" + " " + ru.getUserNo());
                }

            });

            //Remove user when user is not in remote list
            List adminProfileIds = newArrayList(1, 4, 7, 9, 11, 13);

            users.forEach(u -> {
                UserInfoOnMes fitUser = remoteDirectUser.stream()
                        .filter(ur -> (ur.getUserNo().equals(u.getJobnumber())))
                        .findFirst().orElse(null);

                UserProfile usersAdminProfile = u.getUserProfiles().stream()
                        .filter(p -> adminProfileIds.contains(p.getId()))
                        .findFirst()
                        .orElse(null);
                
                //If user is not in admin group, update user to delete status
                if (fitUser == null && usersAdminProfile == null) {
                    System.out.println("remove" + " " + u.getJobnumber());
                    u.setState(State.DELETED);
                    userDAO.update(u);
                }

            });
        }

    }

    private void setUserProfle(User user, String department) {
        UserProfile preAssyRole = userProfiles.stream().filter(p -> p.getId() == 19).findFirst().orElse(null);
        UserProfile assyRole = userProfiles.stream().filter(p -> p.getId() == 14).findFirst().orElse(null);
        UserProfile pkgRole = userProfiles.stream().filter(p -> p.getId() == 15).findFirst().orElse(null);
        UserProfile testRole = userProfiles.stream().filter(p -> p.getId() == 16).findFirst().orElse(null);

        Set<UserProfile> roles = user.getUserProfiles();
        roles.remove(preAssyRole);
        roles.remove(assyRole);
        roles.remove(testRole);
        roles.remove(pkgRole);
//"ASSY_USER", "PREASSY_USER", "PACKING_USER"

        switch (department) {
            case "A":
                roles.add(preAssyRole);
                break;
            case "B":
                roles.add(assyRole);
                break;
            case "T":
                roles.add(testRole);
                break;
            case "P":
                roles.add(pkgRole);
                break;
            default:
                break;
        }

        user.setUserProfiles(roles);
    }

    private boolean isDateInRecent(Date d) {
        return new DateTime(d).isAfter(new DateTime().minusDays(7));
    }

}
