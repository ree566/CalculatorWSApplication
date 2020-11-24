package com.advantech.model.db1;
// Generated 2017/6/27 下午 04:37:51 by Hibernate Tools 4.3.1

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * UserNotification generated by hbm2java
 */
@Entity
@Table(name = "User_Notification",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
public class UserNotification implements java.io.Serializable {

    private int id;
    private String name;
    private String description;
    private int enabled;

    @JsonIgnore
    private Set<User> users = new HashSet<User>(0);

    public UserNotification() {
    }

    public UserNotification(int id, String name, int enabled) {
        this.id = id;
        this.name = name;
        this.enabled = enabled;
    }

    public UserNotification(int id, String name, String description, int enabled, Set<User> users) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.users = users;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "[name]", nullable = false, length = 50)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "[description]", length = 200)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "[enabled]", nullable = false)
    public int getEnabled() {
        return this.enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "User_Notification_REF", joinColumns = {
        @JoinColumn(name = "user_notification_id", nullable = false, insertable = false, updatable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "[user_id]", nullable = false, insertable = false, updatable = false)})
    public Set<User> getUsers() {
        return this.users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public boolean enabled() {
        return this.enabled == 1;
    }

}
