package kr.modusplant.domains.account.social.framework.outbound.client;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.framework.outbound.exception.UnsupportedSocialProviderException;
import kr.modusplant.domains.account.social.usecase.port.client.SocialAuthClient;
import kr.modusplant.domains.account.social.usecase.port.client.SocialAuthClientFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SocialAuthClientFactoryImpl implements SocialAuthClientFactory {
    private final Map<SocialProvider, SocialAuthClient> clientMap;

    public SocialAuthClientFactoryImpl(List<SocialAuthClient> clients) {
        this.clientMap = clients.stream()
                .collect(Collectors.toMap(
                        SocialAuthClient::getProvider,
                        Function.identity()
                ));
    }

    @Override
    public SocialAuthClient getClient(SocialProvider provider) {
        return Optional.ofNullable(clientMap.get(provider))
                .orElseThrow(UnsupportedSocialProviderException::new);
    }
}
