package com.example.hairSalonBooking.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String type;

    // là Large Object có nghía là dữ liệu rắt lớn
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imageData;
    //BLOB là Binary large object

    @ManyToOne(cascade = CascadeType.ALL) // Thêm CascadeType.ALL
    private Customer customer;
}
