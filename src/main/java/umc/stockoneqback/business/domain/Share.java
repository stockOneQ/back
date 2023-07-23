package umc.stockoneqback.business.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.stockoneqback.global.base.BaseTimeEntity;
import umc.stockoneqback.global.base.Status;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
/*
 * TODO : file type change required
 * */
@Table(name = "share")
public class Share extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String file;

    private String content;

    @Convert(converter = Category.CategoryConverter.class)
    private Category category;

    @Convert(converter = Status.StatusConverter.class)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    private Business business;
}
