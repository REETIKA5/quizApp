package com.quiz.Backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(unique = true, nullable = false)
    private String username;


    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;


    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String pictureUrl;
    private String phoneNumber;
    private Integer age;
    private String address;


    public enum Role {
        ADMIN,
        PLAYER
    }


    public User() {}


    public User(Long id, String username, String email, String password,
                String firstName, String lastName, Role role, String pictureUrl,
                String phoneNumber, Integer age, String address) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.pictureUrl = pictureUrl;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.address = address;
    }


    public Long getId()
    { return id;
    }

    public void setId(Long id)
    { this.id = id;
    }

    public String getUsername()
    { return username;
    }

    public void setUsername(String username)
    { this.username = username;
    }

    public String getEmail()
    { return email;
    }

    public void setEmail(String email)
    { this.email = email;
    }

    public String getPassword()
    { return password;
    }
    public void setPassword(String password)
    { this.password = password;
    }

    public String getFirstName()
    { return firstName; }
    public void setFirstName(String firstName)
    { this.firstName = firstName; }

    public String getLastName()
    { return lastName;
    }
    public void setLastName(String lastName)
    { this.lastName = lastName;
    }

    public Role getRole()
    { return role;
    }
    public void setRole(Role role)
    { this.role = role;
    }

    public String getPictureUrl()
    { return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl)
    { this.pictureUrl = pictureUrl;
    }

    public String getPhoneNumber()
    { return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    { this.phoneNumber = phoneNumber;
    }

    public Integer getAge()
    { return age;
    }

    public void setAge(Integer age)
    { this.age = age;
    }

    public String getAddress()
    { return address;
    }

    public void setAddress(String address)
    { this.address = address;
    }
}
