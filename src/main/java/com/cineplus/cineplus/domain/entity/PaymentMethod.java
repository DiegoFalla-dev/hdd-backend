package com.cineplus.cineplus.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payment_methods")
@Getter
@Setter
@NoArgsConstructor
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Encrypted fields
    @Column(name = "card_number_encrypted", length = 1024)
    private String cardNumberEncrypted; // tarjeta (encrypted)

    @Column(name = "card_holder_encrypted", length = 1024)
    private String cardHolderEncrypted; // titular (encrypted)

    @Column(name = "cci_encrypted", length = 1024)
    private String cciEncrypted; // cci (encrypted)

    @Column(name = "expiry_encrypted", length = 1024)
    private String expiryEncrypted; // vencimiento (encrypted)

    @Column(name = "phone_encrypted", length = 1024)
    private String phoneEncrypted; // celular associated with method (encrypted)

    @Column(name = "is_default")
    private Boolean isDefault = false;
}
