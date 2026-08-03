const glyphMap = {
  angular: 'A',
  css: '#',
  docker: 'D',
  git: 'G',
  html: '<>',
  java: 'J',
  javascript: 'JS',
  kubernetes: 'K8',
  node: 'N',
  python: 'PY',
  react: 'R',
  spring: 'S',
  typescript: 'TS',
  vue: 'V',
};

function TechnologyIcon({ technology }) {
  if (technology.logoUrl) {
    return <span className="technology-icon technology-icon--image"><img src={technology.logoUrl} alt="" /></span>;
  }

  const normalizedName = technology.name.toLocaleLowerCase('tr-TR');
  const match = Object.keys(glyphMap).find((key) => normalizedName.includes(key));
  const glyph = match ? glyphMap[match] : technology.name.slice(0, 2).toLocaleUpperCase('tr-TR');
  return <span className="technology-icon" aria-hidden="true">{glyph}</span>;
}

export default TechnologyIcon;
