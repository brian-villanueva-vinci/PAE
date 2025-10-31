const Navigate = (toUri) => {
  window.history.pushState({}, '', toUri);
  const popStateEvent = new PopStateEvent('popstate', {state: {}});
  dispatchEvent(popStateEvent);
};

export default Navigate;