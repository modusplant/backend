() => {
  // 네이버 자체 서비스 링크는 제외하되, 식물 카페가 있는 cafe.naver.com은 유지한다
  const internalHost = /(^|\.)(naver\.com|naver\.net|pstatic\.net|navercorp\.com)$/;
  const keptHost = /^cafe\.naver\.com$/;
  const links = new Set();
  document.querySelectorAll('a[href^="http"]').forEach((anchor) => {
    try {
      const url = new URL(anchor.href);
      if (!internalHost.test(url.hostname) || keptHost.test(url.hostname)) {
        links.add(url.origin + url.pathname);
      }
    } catch (error) {
      // 잘못된 href는 건너뛴다
    }
  });
  return [...links].sort();
}
