use crate::node::Node;
use crate::token::Token;

pub fn parse<'a>(tokens: &Vec<Token<'a>>) -> Node<'a> {
    parse_expression(tokens, &mut 0).unwrap()
}

fn parse_expression<'a>(tokens: &Vec<Token<'a>>, i: &mut usize) -> Option<Node<'a>> {
    if *i >= tokens.len() {
        return None;
    }

    let n: Node = match tokens[*i] {
        Token::IntegerLiteral(x) => Node::Constant(x),
        Token::Name(y) => {
            if *i + 1 < tokens.len() && tokens[*i + 1] == Token::LeftBracket {
                *i += 1;
                match parse_bracketed_expression(tokens, i) {
                    Node::Bracket { expr } => Node::Function(y, expr),
                    _ => todo!(),
                }
            } else {
                Node::Variable(y)
            }
        }
        Token::LeftBracket => parse_bracketed_expression(tokens, i),
        _ => todo!(),
    };

    *i += 1;
    if *i < tokens.len() && tokens[*i] == Token::Plus {
        *i += 1;
        return Some(Node::Plus(
            Box::new(n),
            Box::new(parse_expression(tokens, i).unwrap()),
        ));
    } else if *i < tokens.len() && tokens[*i] == Token::Minus {
        *i += 1;
        return Some(Node::Minus(
            Box::new(n),
            Box::new(parse_expression(tokens, i).unwrap()),
        ));
    } else if *i < tokens.len() && tokens[*i] == Token::Asterisk {
        *i += 1;
        return Some(Node::Multiply(
            Box::new(n),
            Box::new(parse_expression(tokens, i).unwrap()),
        ));
    } else if *i < tokens.len() && tokens[*i] == Token::Slash {
        *i += 1;
        return Some(Node::Divide(
            Box::new(n),
            Box::new(parse_expression(tokens, i).unwrap()),
        ));
    } else if *i < tokens.len() && tokens[*i] == Token::Circumflex {
        *i += 1;
        return Some(Node::Power(
            Box::new(n),
            Box::new(parse_expression(tokens, i).unwrap()),
        ));
    } else {
        return Some(n);
    }
}

fn parse_bracketed_expression<'a>(tokens: &Vec<Token<'a>>, i: &mut usize) -> Node<'a> {
    if tokens[*i] == Token::LeftBracket {
        *i += 1;
        let n: Node = parse_expression(tokens, i).unwrap();
        if tokens[*i] != Token::RightBracket {
            panic!("Expected right bracket");
        }
        Node::Bracket(Box::new(n))
    } else {
        parse_expression(tokens, i).unwrap()
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use test_case::test_case;

    #[test_case(&vec![Token::LeftBracket,Token::IntegerLiteral(3),Token::RightBracket], Node::Bracket(Box::new(Node::Constant(3))) ; "(3)")]
    #[test_case(&vec![Token::LeftBracket,Token::Name("abc"),Token::RightBracket], Node::Bracket(Box::new(Node::Variable("abc"))) ; "(abc)")]
    #[test_case(&vec![Token::Name("x"),Token::Plus,Token::IntegerLiteral(2)], Node::Plus(Box::new(Node::Variable("x")), Box::new(Node::Constant(2))) ; "x_plus_2")]
    #[test_case(&vec![Token::Name("x"),Token::Minus,Token::IntegerLiteral(2)], Node::Minus(Box::new(Node::Variable("x")), Box::new(Node::Constant(2))) ; "x_minus_2")]
    #[test_case(&vec![Token::Name("x"),Token::Asterisk,Token::IntegerLiteral(2)], Node::Multiply(Box::new(Node::Variable("x")), Box::new(Node::Constant(2))) ; "x_times_2")]
    #[test_case(&vec![Token::Name("x"),Token::Slash,Token::IntegerLiteral(2)], Node::Divide(Box::new(Node::Variable("x")), Box::new(Node::Constant(2))) ; "x_divide_2")]
    #[test_case(&vec![Token::Name("x"),Token::Circumflex,Token::IntegerLiteral(2)], Node::Power(Box::new(Node::Variable("x")), Box::new(Node::Constant(2))) ; "x_power_2")]
    #[test_case(&vec![Token::LeftBracket,Token::IntegerLiteral(1),Token::Plus,Token::IntegerLiteral(2),Token::RightBracket,Token::Plus,Token::IntegerLiteral(3)], Node::Plus(Box::new(Node::Bracket(Box::new(Node::Plus(Box::new(Node::Constant(1)),Box::new(Node::Constant(2)))))), Box::new(Node::Constant(3))) ; "(1+2)+3")]
    #[test_case(&vec![Token::Name("sin"),Token::LeftBracket,Token::Name("x"),Token::RightBracket], Node::Function("sin",Box::new(Node::Variable("x"))) ; "sin(x)")]
    fn parse_brackets(input: &Vec<Token>, output: Node) {
        assert_eq!(parse(input), output);
    }
}
