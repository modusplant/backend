() => {
  // 서비스 종료 여부와 로그인 요구 여부를 판단할 수 있도록 페이지의 접근 상태 신호만 반환한다
  const text = (document.body ? document.body.innerText : '').replace(/\s+/g, ' ').trim();
  return {
    url: location.href,
    title: document.title,
    hasPasswordInput: document.querySelector('input[type="password"]') !== null,
    linkCount: document.querySelectorAll('a[href]').length,
    text: text.slice(0, 800),
  };
}
