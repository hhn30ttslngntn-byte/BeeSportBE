package com.example.sport_be.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncodeUtil {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        //cach su dung:
        //1: nhap mk ban muon vao o rawpassword
        //2: chay file nay
        //3: chay chuong trinh se co mk duoc ma hoa duoi dang ma hash
        //4: dan doan mk hash vao sql tao ng dung cua ban
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "123456";//tự chỉnh theo ý thích
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println(encodedPassword);
    }
}
