package kr.modusplant.infrastructure.event.consumer;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.repository.CommLikeRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberTermRepository;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.shared.event.AgreedTermsOfVersionSaveEvent;
import kr.modusplant.shared.event.MemberSaveEvent;
import kr.modusplant.shared.event.PostLikeEvent;
import kr.modusplant.shared.event.PostUnlikeEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NormalIdentityEventConsumer {
    private final SiteMemberRepository memberRepository;
    private final SiteMemberTermRepository memberTermRepository;

    public NormalIdentityEventConsumer(EventBus eventBus, SiteMemberTermRepository commLikeRepository, SiteMemberTermRepository memberTermRepository, SiteMemberRepository memberRepository) {
        this.memberTermRepository = memberTermRepository;
        this.memberRepository = memberRepository;
        eventBus.subscribe(event -> {
            if (event instanceof MemberSaveEvent memberSaveEvent) {
                putMemberSaveEvent(
                        memberSaveEvent.getEmail(),
                        memberSaveEvent.getPassword(),
                        memberSaveEvent.getNickname());
            }
        });
        eventBus.subscribe(event -> {
            if (event instanceof AgreedTermsOfVersionSaveEvent agreedTermsOfVersionSaveEvent) {
                putAgreedTermsOfVersionSaveEvent(
                        agreedTermsOfVersionSaveEvent.getMemberActiveUuid(),
                        agreedTermsOfVersionSaveEvent.getAgreedTermsOfUseVersion(),
                        agreedTermsOfVersionSaveEvent.getAgreedPrivacyPolicyVersion(),
                        agreedTermsOfVersionSaveEvent.getAgreedAdInfoReceivingVersion());
            }
        });
        eventBus.subscribe(event -> {
            if (event instanceof AgreedTermsOfVersionSaveEvent agreedTermsOfVersionSaveEvent) {
                putAgreedTermsOfVersionSaveEvent(
                        agreedTermsOfVersionSaveEvent.getMemberActiveUuid(),
                        agreedTermsOfVersionSaveEvent.getAgreedTermsOfUseVersion(),
                        agreedTermsOfVersionSaveEvent.getAgreedPrivacyPolicyVersion(),
                        agreedTermsOfVersionSaveEvent.getAgreedAdInfoReceivingVersion());
            }
        });
    }

    private void putMemberSaveEvent(String email, String password, String nickname) {
        memberRepository.save(SiteMemberEntity.builder().nickname(nickname).build());
    }

    private void putAgreedTermsOfVersionSaveEvent(UUID memberActiveUuid,
                                                  String agreedTermsOfUseVersion,
                                                  String agreedPrivacyPolicyVersion,
                                                  String agreedAdInfoReceivingVersion) {

    }
}
