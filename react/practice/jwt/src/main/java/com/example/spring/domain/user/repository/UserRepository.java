package com.example.spring.domain.user.repository;

import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.example.spring.domain.auth.dto.request.LoginRequest;
import com.example.spring.domain.user.dto.request.SignUpRequest;
import com.example.spring.domain.user.entity.User;

@Mapper
public interface UserRepository {

	@Select("SELECT u.uuid, u.email, u.password, u.name, u.role_uuid, r.uuid as role_uuid, r.name as role_name "
			+ "FROM users as u JOIN roles as r ON u.role_uuid = r.uuid "
			+ "WHERE u.email=#{email} AND u.password=#{password}")
	@Results({
		@Result(property = "uuid", column = "uuid"),
		@Result(property = "email", column = "email"),
		@Result(property = "password", column = "password"),
		@Result(property = "name", column = "name"),
		@Result(property = "role.uuid", column = "role_uuid"),
		@Result(property = "role.name", column = "role_name"),
	})
	Optional<User> findByEmailAndPassword(LoginRequest loginRequest);


	@Insert("INSERT INTO users (uuid, email, password, name, role_uuid) VALUES "
			+ "(UUID_TO_BIN(UUID()), #{email}, #{password}, #{name}, (SELECT uuid FROM roles WHERE name = '일반'))")
	void save(SignUpRequest signUpRequest);

	@Select("SELECT u.uuid, u.email, u.password, u.name, u.role_uuid, r.uuid as role_uuid, r.name as role_name FROM users as u JOIN roles as r ON u.role_uuid = r.uuid WHERE email=#{email}")
    @Results({
        @Result(property = "uuid", column = "uuid"),
        @Result(property = "email", column = "email"),
        @Result(property = "password", column = "password"),
        @Result(property = "name", column = "name"),
        @Result(property = "role.uuid", column = "role_uuid"),
        @Result(property = "role.name", column = "role_name"),
    })
    Optional<User> findByEmail(String email);
}
